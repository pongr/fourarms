name := "fourarms"

organization := "com.pongr"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-slf4j" % "0.6.9",
  "ch.qos.logback" % "logback-classic" % "1.0.6",
  "org.apache.james" % "apache-mailet-base" % "1.1",
  "commons-lang" % "commons-lang" % "2.5",
  "commons-io" % "commons-io" % "1.3.2",
  "com.amazonaws" % "aws-java-sdk" % "1.3.10",
  "com.rabbitmq" % "amqp-client" % "2.8.2",
  "org.specs2" %% "specs2" % "1.11" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test",
  "org.apache.james" % "james-server-core" % "3.0-beta3" % "test",
  "com.yammer.metrics" % "metrics-core" % "2.1.2",
  "com.yammer.metrics" % "metrics-graphite" % "2.1.2"
)

seq(sbtrelease.Release.releaseSettings: _*)

//http://www.scala-sbt.org/using_sonatype.html
//https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots/")
  else                             Some("releases" at nexus + "service/local/staging/deploy/maven2/")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

homepage := Some(url("http://github.com/pongr/fourarms"))

organizationName := "Pongr"

organizationHomepage := Some(url("http://pongr.com"))

description := "A collection of Scala-based mailets and matchers for Apache James"

pomExtra := (
  <scm>
    <url>git@github.com:pongr/fourarms.git</url>
    <connection>scm:git:git@github.com:pongr/fourarms.git</connection>
  </scm>
  <developers>
    <developer>
      <id>pcetsogtoo</id>
      <name>Byamba Tumurkhuu</name>
      <url>http://pongr.com</url>
    </developer>
    <developer>
      <id>zcox</id>
      <name>Zach Cox</name>
      <url>http://theza.ch</url>
    </developer>
  </developers>
)
