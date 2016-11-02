package com.wojtechnology.sentiment

import org.apache.log4j.LogManager
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.feature._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._


/** Main object for SentimentAnalysis tool */
object SentimentAnalysis {
  val log = LogManager.getLogger("com.wojtechnology.sentiment")

  /** Main function for program */
  def main(args: Array[String]) {
    if (args.length < 2) {
      log.error("Did not provide a train and test data")
    } else {
      val spark = initSpark("SentimentAnalysis")
      val trainPath = args(0)
      val testPath = args(1)

      import spark.implicits._

      val productLabels = (polarity: Double) => if (polarity == 4.0) 1.0 else 0.0
      val udfProductLabels = udf(productLabels).apply(col("polarity"))

      log.info("Reading training data...")
      val trainDf = readCSV(spark, trainPath)
      val trainDfTokenized = tokenize(trainDf)
      val trainDfCleaned = clean(trainDfTokenized)

      val tfidfVectorizer = new TfidfVectorizer()
        .setInputCol("tokensClean")
        .setOutputCol("features")
        .fit(trainDfCleaned)
      tfidfVectorizer.save("tfidf.dat")

      val trainDfTransformed = tfidfVectorizer
        .transform(trainDfCleaned)
        .withColumn("labels", udfProductLabels)

      val clf = new NaiveBayes()
        .setFeaturesCol("features")
        .setLabelCol("labels")
        .fit(trainDfTransformed)

      val testDf = readCSV(spark, testPath).filter(row => row.getDouble(0) != 2.0)

      val testDfTokenized = tokenize(testDf)
      val testDfCleaned = clean(testDfTokenized)
      val testDfTransformed = tfidfVectorizer
        .transform(testDfCleaned)
        .withColumn("labels", udfProductLabels)

      val predictions = clf.transform(testDfTransformed)

      val testCount = testDf.count
      val correctCount = predictions.select("labels", "prediction").filter(row => {
        row.getDouble(0) == row.getDouble(1)
      }).count
      val correct = correctCount.toFloat / testCount.toFloat
      log.info(s"Correct: $correct, $correctCount / $testCount")
    }
  }

  def tokenize(df: DataFrame): DataFrame = {
    val tokenizer = new Tokenizer().setInputCol("rawText").setOutputCol("tokens")
    tokenizer.transform(df)
  }

  /**
    * Cleans dataframe "tokens" column by removing stop words
    *
    * @param df
    * @return
    */
  def clean(df: DataFrame): DataFrame = {
    val remover = new StopWordsRemover().setInputCol("tokens").setOutputCol("tokensClean")
    remover.transform(df)
  }

  /**
    * Reads CSV into dataframe
    *
    * @param spark Spark Session
    * @param path CSV path
    * @return DataFrame containing data in CSV
    */
  def readCSV(spark: SparkSession, path: String): DataFrame = {
    val schema = StructType(Seq(
      StructField("polarity", DoubleType, nullable = false),
      StructField("id", LongType, nullable = false),
      StructField("dateString", StringType, nullable = false),
      StructField("query", StringType, nullable = false),
      StructField("user", StringType, nullable = false),
      StructField("rawText", StringType, nullable = false)
    ))

    spark.sqlContext.read
      .format("com.databricks.spark.csv")
      .schema(schema)
      .load(path)
  }

  /**
    * Init SparkContext with given app name
    *
    * @param appName Name of Spark Application
    * @return SparkContext for use in program
    */
  def initSpark(appName: String): SparkSession = {
    SparkSession.builder().appName(appName).getOrCreate()
  }
}
