
name := "codelist-api-service"

description := "The APIs to use upload, download codelists"

version := "0.1"

scalaVersion := "2.11.8"

enablePlugins(ServicePlugin)

scalacOptions ++= Seq("-feature", "-deprecation")

parallelExecution in Test := false

libraryDependencies ++= {

  Seq(
    "com.typesafe.akka" %% "akka-http" % "10.1.5",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5",
    "org.scalatest" %% "scalatest" % "2.2.6" % Test
  )
}