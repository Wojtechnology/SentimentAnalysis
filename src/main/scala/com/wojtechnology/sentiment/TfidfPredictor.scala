package com.wojtechnology.sentiment

import org.apache.spark.ml.{Estimator, Pipeline, PipelineModel}
import org.apache.spark.ml.feature.{CountVectorizer, IDF, StopWordsRemover, Tokenizer}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Uses Tfidf model to generate vectors from most important words in the corpus.
  * TODO(wojtek): accept params for size of TokenVectorizer
  */
class TfidfPredictor(override val uid: String) extends Estimator[PipelineModel]
  with HasInputCol with HasOutputCol {

  type TokenVectorizer = CountVectorizer

  /**
    * Randomly generates uid
    *
    * @return instance of TfidfVectorizer
    */
  def this() = this(Identifiable.randomUID("tfidf"))

  /**
    * Sets input column string.
    *
    * @param value
    */
  def setInputCol(value: String): this.type = set(inputCol, value)

  /**
    * Sets output column string.
    *
    * @param value
    */
  def setOutputCol(value: String): this.type = set(outputCol, value)

  /**
    * Trains TFIDF model and returns it.
    *
    * @param dataset
    * @return Trained TFIDF pipeline model
    */
  override def fit(dataset: Dataset[_]): PipelineModel = {
    transformSchema(dataset.schema, logging = true)

    val inputColName = $(inputCol)
    val outputColName = $(outputCol)

    val tokenVectorizer = new TokenVectorizer()
      .setInputCol(inputColName)
      .setOutputCol("featuresRaw")
    val idf = new IDF()
      .setInputCol("featuresRaw")
      .setOutputCol(outputColName)
    val pipeline = new Pipeline().setStages(Array(tokenVectorizer, idf))

    pipeline.fit(dataset)
  }

  /**
    * Transforms dataset schema.
    *
    * @param schema
    * @return transformed schema
    */
  override def transformSchema(schema: StructType): StructType = {
    val inputColName = $(inputCol)
    val outputColName = $(outputCol)

    require(schema(inputColName).dataType.isInstanceOf[ArrayType],
      s"Input column must be of type NumericType but got ${schema(inputColName).dataType}.")
    require(!schema.fields.exists(_.name == outputColName),
      s"Output column $outputColName already exists.")

    StructType(schema.fields :+ StructField(outputColName,
      new ArrayType(IntegerType, containsNull = false), false))
  }

  /**
    * Copies instance of the estimator.
    *
    * @param extra
    * @return the copy
    */
  override def copy(extra: ParamMap): TfidfPredictor = defaultCopy(extra)
}

/**
  * Contains code for training and predicting using the Tfidf Vectorizer
  */
object TfidfPredictor {

  /**
    * Trains a tfidf model
    * @param options
    * @param spark
    * @return Pipeline model that performs tfidf
    */
  def train(options: CommandLineOptions, spark: SparkSession): PipelineModel = {
    val trainPath = options.getString(CommandLineParser.TRAIN_FILE_OPTION).get
    val outputPath = options.getString(CommandLineParser.OUTPUT_FILE_OPTION).get

    val trainDf = TwitterCSVReader.read(spark, trainPath)

    // Tokenize raw text
    val tokenizer = new Tokenizer().setInputCol("rawText").setOutputCol("tokens")
    val trainDfTokenized = tokenizer.transform(trainDf)

    // Remove stop words
    val remover = new StopWordsRemover().setInputCol("tokens").setOutputCol("tokensClean")
    val trainDfCleaned = remover.transform(trainDfTokenized)

    val tfidfVectorizer = new TfidfPredictor()
      .setInputCol("tokensClean")
      .setOutputCol("features")
      .fit(trainDfCleaned)
    tfidfVectorizer.save(outputPath)
    tfidfVectorizer
  }
}
