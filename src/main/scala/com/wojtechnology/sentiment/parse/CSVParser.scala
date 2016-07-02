package com.wojtechnology.sentiment.parse

import org.apache.spark.rdd.RDD

/**
  * Parses comma separated lines (usually from CSV files)
  */
class CSVParser extends AbstractParser[Seq[String]] {

  /**
    * Splits text in each line by commas
    */
  override def parse(rDD: RDD[String]): RDD[Seq[String]] = {
    rDD.map(line => line.split(","))
  }

}
