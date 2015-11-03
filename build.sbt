name := """scala-wiki"""

version := "1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test")

scalaVersion := "2.11.7"

libraryDependencies += "com.scalawilliam" %% "xs4s" % "0.2-SNAPSHOT"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "org.scala-lang" % "scala-xml" % "2.11.0-M4"

libraryDependencies += "eu.cdevreeze.yaidom" %% "yaidom" % "1.4.2"

libraryDependencies += "org.sweble.wikitext" % "swc-parser-lazy" % "2.0.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-pickling" % "0.10.1"

libraryDependencies ++= Seq(
    "net.debasishg" %% "redisclient" % "3.0"
)

resolvers += "pico" at "http://reposerver/maven/"

libraryDependencies += "eu.picoweb" %% "spider-link-consumer" % "1.1-SNAPSHOT"

libraryDependencies += "org.mongodb" %% "casbah" % "2.8.2"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.4"