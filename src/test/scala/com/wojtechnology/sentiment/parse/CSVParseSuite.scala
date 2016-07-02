package com.wojtechnology.sentiment.parse

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

/**
  * Testing CSV parsing
  */
class CSVParseSuite extends FunSuite with SharedSparkContext {
  test("CSV lines are parsed properly") {
    val input = Seq("this,is,a,test", "csv,for,csv,parser")
    val expected = Seq(Seq("this","is","a","test"), Seq("csv","for","csv","parser"))
    val parser = new CSVParser
    assert(parser.parse(sc.parallelize(input)).collect().toList == expected)
  }
}
