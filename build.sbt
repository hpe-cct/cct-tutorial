name := "cogx-tutorial"

description := "Example code to go along with the CogX tutorial."

organizationName := "Hewlett Packard Labs"

organizationHomepage := Some(url("http://www.labs.hpe.com"))

version := "0.1.0"

organization := "com.hpe.cct"

scalaVersion := "2.11.7"

fork in run := true

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "com.hpe.cct" %% "cogx-nn" % "2.0.0-alpha.1",
  "com.hpe.cct" %% "cogx-sandbox" % "1.2.8",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "junit" % "junit" % "4.7" % "test"
)
