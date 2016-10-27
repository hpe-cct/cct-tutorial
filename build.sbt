name := "cct-tutorial"

description := "Example code to go along with the CCT tutorial."

organizationName := "Hewlett Packard Labs"

organizationHomepage := Some(url("http://www.labs.hpe.com"))

organization := "com.hpe.cct"

scalaVersion := "2.11.7"

fork in run := true

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "com.hpe.cct" %% "cct-nn" % "2.0.0-alpha.4",
  "com.hpe.cct" %% "cct-sandbox" % "1.2.11",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "junit" % "junit" % "4.7" % "test"
)

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

resolvers += Resolver.jcenterRepo
