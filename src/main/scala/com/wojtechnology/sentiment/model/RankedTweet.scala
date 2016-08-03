package com.wojtechnology.sentiment.model

/**
  * RankedTweet represents a tweet that has been ranked for sentiment
  */
class RankedTweet(val id: Int,
                  val when: java.time.ZonedDateTime,
                  val query: String,
                  val user: String,
                  val rawText: String,
                  val polarity: Int) {

}
