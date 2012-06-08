name := "fourarms"

organization := "com.pongr"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases"
)


libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.11" % "test",
  "org.apache.james" % "apache-mailet-base" % "1.0",
  "commons-lang" % "commons-lang" % "2.5",
  "com.amazonaws" % "aws-java-sdk" % "1.3.10"
)
