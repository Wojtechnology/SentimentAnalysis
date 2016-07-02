package com.wojtechnology.sentiment.parse

import org.apache.spark.rdd.RDD

/**
  * Trait for filtering columns in sequence of strings
  */
trait ColumnsFilter {

  /**
    * Filters by column indices
    * Returns values in order of indices given
    */
  def filter(rDD: RDD[Seq[String]], idxs: Seq[Int]): RDD[Seq[String]] = {
    rDD.map(line => idxs.map(line))
  }

}
