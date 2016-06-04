package com.wojtechnology.sentiment.parse

import io.Source

/** Methods for parsing CSV files */
object CSVParser {

  /** Parses a CSV file and returns a List of Lists containing contents */
  def parse(bufferedSource: Source): Seq[Seq[String]] = {
    bufferedSource.getLines.foldLeft(
      Seq[Seq[String]]())((a, b) => a :+ b.split(",").map(_.trim).toList
    )
  }

}
