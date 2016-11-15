package com.wojtechnology.sentiment

import org.apache.log4j.LogManager
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.feature._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._


/** Main object for SentimentAnalysis tool */
object SentimentAnalysis {
  val log = LogManager.getLogger("com.wojtechnology.sentiment")

  /** Main function for program */
  def main(args: Array[String]) {
    CommandLineParser.parseAndValidate(args) match {
      case Some(options) => {
        val spark = initSpark("SentimentAnalysis")

        (options.getString(CommandLineParser.MODEL_OPTION),
            options.getString(CommandLineParser.ACTION_OPTION)) match {
          case (Some("tfidf"), Some("fit")) => {
            TfidfPredictor.train(options, spark)
          }
          case _ =>
            // NB(wojtek) if this happens, it means that parseAndValidate is inconsistent with this
            // piece of code.
        }
      }
      case None => {
        CommandLineParser.printUsage
        System.exit(1)
      }
    }
  }

  def oldCode(spark: SparkSession, args: Array[String]) = {
    val trainPath = args(0)
    val testPath = args(1)

    val productLabels = (polarity: Double) => if (polarity == 4.0) 1.0 else 0.0
    val udfProductLabels = udf(productLabels).apply(col("polarity"))

    log.info("Reading training data...")
    val trainDf = TwitterCSVReader.read(spark, trainPath)
    val trainDfTokenized = tokenize(trainDf)
    val trainDfCleaned = clean(trainDfTokenized)

    val tfidfVectorizer = new TfidfPredictor()
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

    val testDf = TwitterCSVReader.read(spark, testPath).filter(row => row.getDouble(0) != 2.0)

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
    * Init SparkContext with given app name
    *
    * @param appName Name of Spark Application
    * @return SparkContext for use in program
    */
  def initSpark(appName: String): SparkSession = {
    SparkSession.builder().appName(appName).getOrCreate()
  }
}
