name := """veon-recruit"""
organization := "prv.walidus"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
libraryDependencies += "com.moviejukebox" % "api-imdb" % "1.5"
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "prv.walidus.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "prv.walidus.binders._"
