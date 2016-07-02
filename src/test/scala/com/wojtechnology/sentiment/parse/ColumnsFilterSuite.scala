package com.wojtechnology.sentiment.parse

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.rdd.RDD
import org.scalatest.FunSuite

/**
  * Created by wojtekswiderski on 2016-07-02.
  */
class ColumnsFilterSuite extends FunSuite with SharedSparkContext {
  test("Test that columns are filtered correctly") {
    val input_list = Seq(Seq("r0c0", "r0c1", "r0c2"), Seq("r1c0", "r1c1", "r1c2"))
    val input_idxs = Seq(0, 2)
    val expected = Seq(Seq("r0c0", "r0c2"), Seq("r1c0", "r1c2"))
    val parser = new AbstractParser[String] with ColumnsFilter {
      def parse(rDD: RDD[String]): RDD[String] = rDD
    }
    assert(parser.filter(sc.parallelize(input_list), input_idxs).collect().toList == expected)
  }
  test("Test that columns are ordered correctly") {
    val input_list = Seq(Seq("r0c0", "r0c1", "r0c2"), Seq("r1c0", "r1c1", "r1c2"))
    val input_idxs = Seq(2, 0)
    val expected = Seq(Seq("r0c2", "r0c0"), Seq("r1c2", "r1c0"))
    val parser = new AbstractParser[String] with ColumnsFilter {
      def parse(rDD: RDD[String]): RDD[String] = rDD
    }
    assert(parser.filter(sc.parallelize(input_list), input_idxs).collect().toList == expected)
  }
}
