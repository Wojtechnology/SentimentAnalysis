package com.wojtechnology.sentiment.parse

import com.wojtechnology.sentiment.actions.FileStreamAction
import org.scalatest.FunSuite

/**
  * Created by wojtekswiderski on 2016-07-01.
  */
class ArgumentParserSuite extends FunSuite {
  test("Testing if argument parser correctly parses argument") {
    val args = Seq("-f")
    val actions = ArgumentParser.parse(args)
    assert(actions.size == 1, "")
    assert(actions.head.isInstanceOf[FileStreamAction])
  }
}
