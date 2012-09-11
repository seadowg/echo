// Project
name := "echo"

version := "1.1.0"

// Scala
scalaVersion := "2.9.1"

libraryDependencies += "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"

// Tests
parallelExecution in Test := false