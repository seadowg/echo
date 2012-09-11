name := "echo"

organization := "com.github.oetzi"

version := "1.1.0"

scalaVersion := "2.9.2"

// Disable parallel execution of tests.
parallelExecution in Test := false

// Disable parallel execution of tests within a specification.
testOptions in Test += Tests.Argument("sequential")

libraryDependencies += "org.specs2" %% "specs2" % "1.12.1" % "test"
