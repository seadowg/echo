import sbt._
import sbt.Keys._

// SBT build definition for echo.
object EchoBuild extends Build {

  lazy val echo = Project(

    id = "echo",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(

      name := "echo",
      organization := "com.github.oetzi",
      version := "1.1.0",
      scalaVersion := "2.9.2",

      // Disable parallel execution of tests.
      parallelExecution in Test := false,
      testOptions in Test += Tests.Argument("sequential"),

      libraryDependencies ++= Seq(
        // Test dependencies.
        "org.specs2" %% "specs2" % "1.12.1" % "test"
      )
    )
  )
}
