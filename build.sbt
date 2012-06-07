name := "fourarms"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases"
)


libraryDependencies ++= Seq(
  "org.scala-tools.testing" %% "specs" % "1.6.9" % "test",
  "org.apache.james" % "apache-mailet-base" % "1.0",
  "commons-lang" % "commons-lang" % "2.5"
)
