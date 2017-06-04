name := """nrt-demo-backend"""

version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"
resolvers ++= Seq(
  "Bintray" at "https://dl.bintray.com/spark-packages/maven/"
)
libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
  "org.apache.spark" % "spark-sql_2.11" % "2.1.0",
  "org.apache.spark" % "spark-core_2.11" % "2.1.0",
  "databricks" % "spark-corenlp" % "0.2.0-s_2.11",
  "org.codehaus.janino" % "janino" % "3.0.7",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.4",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7",
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.0",
  "org.twitter4j" % "twitter4j-stream" % "3.0.3",
  "com.rabbitmq" % "amqp-client" % "3.6.5",
  "org.scala-lang.modules" %% "scala-async" % "0.9.6",
  "net.debasishg" %% "redisclient" % "3.4",
  "com.danielasfregola" %% "twitter4s" % "0.2.1"

)

