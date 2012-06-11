name := "fourarms"

organization := "com.pongr"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases"
)


libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.11",
  "org.mockito" % "mockito-all" % "1.9.0",
  "org.apache.james" % "apache-mailet-base" % "1.0",
  "commons-lang" % "commons-lang" % "2.5",
  "com.amazonaws" % "aws-java-sdk" % "1.3.10",
  "com.rabbitmq" % "amqp-client" % "2.8.2"
)
