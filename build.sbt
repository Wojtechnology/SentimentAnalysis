name := "SentimentAnalysis"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test",
  "org.apache.spark" %% "spark-core" % "2.0.1",
  "org.apache.spark" %% "spark-mllib" % "2.0.1",
  "com.holdenkarau" %% "spark-testing-base" % "2.0.1_0.4.7" % "test",
  "com.databricks" % "spark-csv_2.11" % "1.4.0"
)
