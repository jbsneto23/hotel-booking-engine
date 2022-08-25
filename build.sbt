name := """hotel-booking-engine"""
organization := "br.com.jbsneto"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  guice,
  "com.h2database" % "h2" % "1.4.200",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
)

// Testing Dependencies
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test
)

