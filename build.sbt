ThisBuild / scalaVersion := "2.13.12"

ThisBuild / organization := "com.webCrawler"

lazy val sharedSettings = Seq(
  resolvers ++= Seq(
    "Akka library repository" at "https://repo.akka.io/maven",
    "Confluent Maven Repository" at "https://packages.confluent.io/maven/",
    "Java.net Maven2 Repository" at "https://download.java.net/maven/2/"
  ),
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Ywarn-dead-code" // Warn when dead code is identified.
  )
)

val akkaVersion = "2.9.2"
val ahcVersion = "1.9.40"
val jSoupVersion = "1.17.2"
val logbackVersion =  "1.4.14"

lazy val webCrawler = (project in file("."))
  .aggregate(core)
  .settings(sharedSettings)

lazy val core = (project in file("core"))
  .settings(
    sharedSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.ning" % "async-http-client" % ahcVersion,
      "org.jsoup" % "jsoup" % jSoupVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
    )
  )
