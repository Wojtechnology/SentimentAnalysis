name := "SentimentAnalysis"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test",
  "org.apache.spark" %% "spark-core" % "2.0.0",
  "org.apache.spark" %% "spark-mllib" % "2.0.0",
  "com.holdenkarau" %% "spark-testing-base" % "2.0.0_0.4.4" % "test",
  "com.databricks" % "spark-csv_2.11" % "1.4.0"
)
