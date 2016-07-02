name := "SentimentAnalysis"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test",
  "org.apache.spark" %% "spark-core" % "1.6.2"
)