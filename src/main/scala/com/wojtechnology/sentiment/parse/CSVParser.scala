package com.wojtechnology.sentiment.parse

/**
  * Created by wojtekswiderski on 2016-05-31.
  */
object CSVParser {
  def parse(path: String): Seq[Seq[String]] = {
    val bufferedSource = io.Source.fromFile(path)
    bufferedSource.getLines.foldRight(Seq[Seq[String]]())((b, a) => a :+ b.split(",").map(_.trim).toSeq)
  }
}
