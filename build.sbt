name := "SentimentAnalysis"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "3.0.0" % "test",
  "org.apache.spark" %% "spark-core" % "1.6.2",
  "org.apache.spark" %% "spark-mllib" % "1.6.2",
  "com.holdenkarau" %% "spark-testing-base" % "1.6.1_0.3.3"
)