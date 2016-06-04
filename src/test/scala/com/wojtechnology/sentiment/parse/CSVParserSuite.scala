package com.wojtechnology.sentiment.parse

import io.Source

import org.scalatest.FunSuite

/**
  * Created by wojtekswiderski on 2016-05-31.
  */
class CSVParserSuite extends FunSuite {
  test("Testing if parse correctly parses CSV") {
    val source = Source.fromURL(getClass.getResource("/testcsv.csv"))
    val items = CSVParser.parse(source)
    assert(items == List(List("this", "is", "a", "test"), List("csv", "for", "csv", "parser")))
  }
}
