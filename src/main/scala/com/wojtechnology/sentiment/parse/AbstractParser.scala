package com.wojtechnology.sentiment.parse

import org.apache.spark.rdd.RDD

/**
  * Abstract class for parsing lines of text from Spark RDDs
  */
abstract class AbstractParser[T] {

  /**
    * Parse method to be implemented
    */
  def parse(rDD: RDD[String]): RDD[T]

}