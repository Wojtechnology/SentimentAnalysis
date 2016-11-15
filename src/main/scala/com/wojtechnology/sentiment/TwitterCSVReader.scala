package com.wojtechnology.sentiment

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types._

/**
  * CSV reader specific to Twitter data.
  */
object TwitterCSVReader {

  /**
    * Reads Twitter data from CSV file into DataFrame
    * @param spark
    * @param path
    * @return DataFrame containing data
    */
  def read(spark: SparkSession, path: String): DataFrame = {
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
}
