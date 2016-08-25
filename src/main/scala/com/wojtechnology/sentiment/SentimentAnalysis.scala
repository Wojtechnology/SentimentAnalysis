package com.wojtechnology.sentiment

import com.wojtechnology.sentiment.parse.CSVParser
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}


case class RankedTweet(polarity: Int,
                       id: Long,
                       dateString: String,
                       query: String,
                       user: String,
                       text: String)

/** Main object for SentimentAnalysis tool */
object SentimentAnalysis {

  /** Main function for program */
  def main(args: Array[String]) {
    if (args.length < 1) {
      println("Did not provide a filename")
    } else {
      val spark = initSpark("SentimentAnalysis")

      import spark.implicits._

      val schema = StructType(Seq(
        StructField("polarity", IntegerType, nullable = false),
        StructField("id", LongType, nullable = false),
        StructField("dateString", StringType, nullable = false),
        StructField("query", StringType, nullable = false),
        StructField("user", StringType, nullable = false),
        StructField("text", StringType, nullable = false)
      ))

      val ds = spark.sqlContext.read
        .format("com.databricks.spark.csv")
        .schema(schema)
        .load(args(0))
        .as[RankedTweet]

      /* ds.show()
      ds.select("polarity").show()
      ds.select($"polarity", $"id" + 1).show()
      ds.filter($"polarity" === 4).show()
      ds.groupBy("polarity").count().show() */

      val allText = ds.select("text").flatMap[String]((attributes: Row) => attributes(0)
        .toString()
        .split(" ")
        .toTraversable)

      allText.groupBy("value").count().orderBy(desc("count")).show()

  }

  def mlLibExample() = {
    val spark = initSpark("SentimentAnalysis")

    // Prepare training data from a list of (label, features) tuples.
    val training = spark.createDataFrame(Seq(
      (1.0, Vectors.dense(0.0, 1.1, 0.1)),
      (0.0, Vectors.dense(2.0, 1.0, -1.0)),
      (0.0, Vectors.dense(2.0, 1.3, 1.0)),
      (1.0, Vectors.dense(0.0, 1.2, -0.5))
    )).toDF("label", "features")

    // Create a LogisticRegression instance. This instance is an Estimator.
    val lr = new LogisticRegression()
    // Print out the parameters, documentation, and any default values.
    println("LogisticRegression parameters:\n" + lr.explainParams() + "\n")

    // We may set parameters using setter methods.
    lr.setMaxIter(10)
      .setRegParam(0.01)

    // Learn a LogisticRegression model. This uses the parameters stored in lr.
    val model1 = lr.fit(training)
    // Since model1 is a Model (i.e., a Transformer produced by an Estimator),
    // we can view the parameters it used during fit().
    // This prints the parameter (name: value) pairs, where names are unique IDs for this
    // LogisticRegression instance.
    println("Model 1 was fit using parameters: " + model1.parent.extractParamMap)

    // We may alternatively specify parameters using a ParamMap,
    // which supports several methods for specifying parameters.
    val paramMap = ParamMap(lr.maxIter -> 20)
      .put(lr.maxIter, 30)  // Specify 1 Param. This overwrites the original maxIter.
      .put(lr.regParam -> 0.1, lr.threshold -> 0.55)  // Specify multiple Params.

    // One can also combine ParamMaps.
    val paramMap2 = ParamMap(lr.probabilityCol -> "myProbability")  // Change output column name.
    val paramMapCombined = paramMap ++ paramMap2

    // Now learn a new model using the paramMapCombined parameters.
    // paramMapCombined overrides all parameters set earlier via lr.set* methods.
    val model2 = lr.fit(training, paramMapCombined)
    println("Model 2 was fit using parameters: " + model2.parent.extractParamMap)

    // Prepare test data.
    val test = spark.createDataFrame(Seq(
      (1.0, Vectors.dense(-1.0, 1.5, 1.3)),
      (0.0, Vectors.dense(3.0, 2.0, -0.1)),
      (1.0, Vectors.dense(0.0, 2.2, -1.5))
    )).toDF("label", "features")

    // Make predictions on test data using the Transformer.transform() method.
    // LogisticRegression.transform will only use the 'features' column.
    // Note that model2.transform() outputs a 'myProbability' column instead of the usual
    // 'probability' column since we renamed the lr.probabilityCol parameter previously.
    model2.transform(test)
      .select("features", "label", "myProbability", "prediction")
      .collect()
      .foreach { case Row(features: Vector, label: Double, prob: Vector, prediction: Double) =>
        println(s"($features, $label) -> prob=$prob, prediction=$prediction")
      }
  }
  }

  /**
    * Init SparkContext with given app name
    * @param appName Name of Spark Application
    * @return SparkContext for use in program
    */
  def initSpark(appName: String): SparkSession = {
    SparkSession.builder().appName(appName).getOrCreate()
  }

  /**
    * Counts number of rows in data containing token and print first 10
    * @param rawData Raw file data
    * @param token token to count
    * @return Unit
    */
  def countTokens(rawData: RDD[String], token: String) = {
    val parser = new CSVParser

    val parsed = parser.parse(rawData)
    val hasSmiley = parsed.filter(_(6).contains(token))
    val countSmiley = hasSmiley.count()

    println(s"Found $countSmiley '$token's")
    hasSmiley.foreach(println)
  }
}