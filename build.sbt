name := "kafka"

version := "0.0.3"

scalaVersion := "2.12.2"

//resolvers += "confluent" at "http://packages.confluent.io/maven/"
//https://github.com/sbt/sbt/issues/3618
val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.0.0"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.1"
libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.1"  // for any java classes looking for this
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.3"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "net.manub" %% "scalatest-embedded-kafka" % "2.0.0" % Test
libraryDependencies += "net.manub" %% "scalatest-embedded-kafka-streams" % "2.0.0" % Test
libraryDependencies += "org.apache.kafka" % "kafka-streams-test-utils" % "2.0.0" % Test
unmanagedBase := baseDirectory.value / "lib"

lazy val root = (project in file(".")).
  enablePlugins(UniversalPlugin).
  enablePlugins(JavaAppPackaging)

fork in run := true