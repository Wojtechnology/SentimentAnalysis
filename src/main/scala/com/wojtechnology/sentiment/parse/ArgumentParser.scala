package com.wojtechnology.sentiment.parse

import com.wojtechnology.sentiment.actions._

/**
  * Created by wojtekswiderski on 2016-06-10.
  */
object ArgumentParser {
  private[this] final val OPEN_FILE = "-f"

  private[this] final val OPTIONS = { OPEN_FILE }

  def parse(args: Array[String]): Array[Action] = {
    args.map()
  }
}
