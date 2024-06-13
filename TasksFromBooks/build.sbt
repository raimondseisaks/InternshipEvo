ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "TasksFromBooks"
  )

libraryDependencies += "org.typelevel" %% "cats-core" % "2.6.1"

