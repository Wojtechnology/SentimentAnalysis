package com.wojtechnology.sentiment

import org.apache.spark.ml.feature.{StopWordsRemover, Tokenizer}
import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

/**
  * Contains code for training and predicting using a Naive Bayes model
  */
object NaiveBayesPredictor {
  val log = SentimentAnalysis.log

  val productLabels = (polarity: Double) => if (polarity == 4.0) 1.0 else 0.0
  val udfProductLabels = udf(productLabels).apply(col("polarity"))

  /**
    * Trains Naive Bayes model to predict sentiment
    * @param options
    * @param spark
    * @return Trained Naive Bayes model
    */
  def train(options: CommandLineOptions, spark: SparkSession): NaiveBayesModel = {
    val trainPath = options.getString(CommandLineParser.TRAIN_FILE_OPTION).get
    val tfidfPath = options.getString(CommandLineParser.TFIDF_FILE_OPTION).get
    val outputPath = options.getString(CommandLineParser.OUTPUT_FILE_OPTION).get

    val trainDf = TwitterCSVReader.read(spark, trainPath)
    val trainDfTokenized = tokenize(trainDf)
    val trainDfCleaned = clean(trainDfTokenized)

    val tfidfVectorizer = TfidfPredictor.load(tfidfPath)

    val trainDfTransformed = tfidfVectorizer
      .transform(trainDfCleaned)
      .withColumn("labels", udfProductLabels)

    val clf = new NaiveBayes()
      .setFeaturesCol("features")
      .setLabelCol("labels")
      .fit(trainDfTransformed)
    clf.save(outputPath)
    clf
  }

  /**
    * Evalulates Naive Bayes model
    * @param options
    * @param spark
    */
  def eval(options: CommandLineOptions, spark: SparkSession) {
    val evalPath = options.getString(CommandLineParser.EVAL_FILE_OPTION).get
    val tfidfPath = options.getString(CommandLineParser.TFIDF_FILE_OPTION).get
    val clfPath = options.getString(CommandLineParser.CLF_FILE_OPTION).get

    val evalDf = TwitterCSVReader.read(spark, evalPath).filter(row => row.getDouble(0) != 2.0)
    val evalDfTokenized = tokenize(evalDf)
    val evalDfCleaned = clean(evalDfTokenized)

    val tfidfVectorizer= TfidfPredictor.load(tfidfPath)
    val evalDfTransformed = tfidfVectorizer
      .transform(evalDfCleaned)
      .withColumn("labels", udfProductLabels)

    val clf = load(clfPath)
    val predictions = clf.transform(evalDfTransformed)

    val evalCount = evalDf.count
    val correctCount = predictions.select("labels", "prediction").filter(row => {
      row.getDouble(0) == row.getDouble(1)
    }).count
    val correct = correctCount.toFloat / evalCount.toFloat
    log.info(s"Correct: $correct, $correctCount / $evalCount")

  }

  /**
    * Loads Naive Bayes model from disk
    * @param path
    * @return
    */
  def load(path: String): NaiveBayesModel = {
    NaiveBayesModel.load(path)
  }

  /**
    * Adds tokens column to dataframe
    * @param df
    * @return
    */
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
}
