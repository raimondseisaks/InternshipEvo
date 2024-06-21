ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "TasksFromBooks"
  )

libraryDependencies += "org.typelevel" %% "cats-core" % "2.6.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.10.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.15.4" % Test
)


