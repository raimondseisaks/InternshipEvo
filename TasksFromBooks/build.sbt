ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

val http4sVersion      = "0.23.18"
val circeVersion       = "0.14.1"
val playVersion        = "2.8.2"
val doobieVersion      = "1.0.0-RC1"
val catsVersion        = "2.12.0"
val catsTaglessVersion = "0.14.0"
val catsEffect3Version = "3.3.0"
val catsEffect2Version = "2.5.4"
val epimetheusVersion  = "0.6.0-M2"
val monixVersion       = "3.4.0"

val akkaVersion          = "2.8.6"
val akkaHttpVersion      = "10.1.11"
val akkaHttpCirceVersion = "1.39.2"

val log4CatsVersion = "2.5.0"

val scalaTestVersion = "3.2.7.0"
val h2Version        = "2.0.202"
val slickVersion     = "3.3.3"
val munitVersion     = "0.7.29"

lazy val root = (project in file("."))
  .settings(
    name := "TasksFromBooks",
    libraryDependencies ++= Seq(
      "org.typelevel"            %% "cats-core"                     % catsVersion,
      "org.typelevel"            %% "cats-effect"                   % catsEffect3Version,
      "org.http4s"               %% "http4s-dsl"                    % http4sVersion,
      "org.http4s"               %% "http4s-ember-server"           % http4sVersion,
      "org.http4s"               %% "http4s-ember-client"           % http4sVersion,
      "org.http4s"               %% "http4s-circe"                  % http4sVersion,
      "org.http4s"               %% "http4s-jdk-http-client"        % "0.9.0",
      "com.typesafe.akka"        %% "akka-http"                     % akkaHttpVersion,
      "de.heikoseeberger"        %% "akka-http-circe"               % akkaHttpCirceVersion,
      "com.typesafe.akka"        %% "akka-stream"                   % akkaVersion,
      "org.typelevel"            %% "log4cats-slf4j"                % log4CatsVersion,
      "ch.qos.logback"            % "logback-classic"               % "1.2.3",
      "org.typelevel"            %% "cats-effect-testing-scalatest" % "1.5.0"          % Test,
      "io.chrisdavenport"        %% "epimetheus-http4s"             % epimetheusVersion,
      "org.scalatestplus"        %% "scalacheck-1-15"               % scalaTestVersion % Test,
      "org.scalatestplus"        %% "selenium-3-141"                % scalaTestVersion % Test,
      "org.typelevel"            %% "simulacrum"                    % "1.0.0",
      "org.tpolecat"             %% "atto-core"                     % "0.8.0",
      "io.circe"                 %% "circe-core"                    % circeVersion,
      "io.circe"                 %% "circe-generic"                 % circeVersion,
      "io.circe"                 %% "circe-generic-extras"          % circeVersion,
      "io.circe"                 %% "circe-optics"                  % circeVersion,
      "io.circe"                 %% "circe-parser"                  % circeVersion,
      "com.typesafe.akka"        %% "akka-actor"                    % akkaVersion,
      "com.typesafe.akka"        %% "akka-persistence"              % akkaVersion,
      "com.typesafe.akka"        %% "akka-cluster"                  % akkaVersion,
      "com.typesafe.akka"        %% "akka-cluster-sharding"         % akkaVersion,
      "org.fusesource.leveldbjni" % "leveldbjni-all"                % "1.8",
      "org.scalameta"            %% "munit"                         % munitVersion     % Test,
      "org.typelevel"            %% "munit-cats-effect"             % "2.0.0-M3"       % Test,
      "org.tpolecat"             %% "doobie-core"                   % doobieVersion,
      "org.tpolecat"             %% "doobie-h2"                     % doobieVersion,
      "org.tpolecat"             %% "doobie-hikari"                 % doobieVersion,
      "org.tpolecat"             %% "doobie-munit"                  % doobieVersion    % Test,
      "com.typesafe.akka"        %% "akka-testkit"                  % akkaVersion      % Test,
      "org.mockito"              %% "mockito-scala"                 % "1.16.32"        % Test,
      "org.scalaj"               %% "scalaj-http"                   % "2.4.2"          % Test,
      "org.tpolecat"             %% "doobie-scalatest"              % doobieVersion    % Test,
      "org.typelevel"            %% "cats-tagless-macros"           % catsTaglessVersion,
      "com.h2database"            % "h2"                            % h2Version,
      "eu.timepit"               %% "refined"                       % "0.9.17",
      "com.typesafe.slick"       %% "slick"                         % slickVersion,
      "org.slf4j"                 % "slf4j-nop"                     % "1.6.4",
      "com.typesafe.slick"       %% "slick-hikaricp"                % slickVersion,
      "com.lihaoyi"              %% "requests"                      % "0.6.5",
      "dev.zio"                  %% "zio"                           % "2.0.12",
      "dev.zio"                  %% "zio-streams"                   % "2.0.12",
      "dev.zio"                  %% "zio-interop-cats"              % "23.0.0.4",
      "io.github.timwspence"     %% "cats-stm"                      % "0.11.0" ,
    )
  )


scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Ymacro-annotations",
)
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.3" cross CrossVersion.full)
mainClass in Compile := Some("reisaks.FinalProject.clientSide.WebSocketClient")

libraryDependencies ++= Seq(
  "io.github.embeddedkafka" %% "embedded-kafka" % "3.6.0",
  "org.apache.kafka" %% "kafka" % "3.7.0",
  "org.apache.kafka" % "kafka-streams" % "3.7.0",
  "org.apache.zookeeper" % "zookeeper" % "3.8.0",
  "org.apache.kafka" % "kafka-streams-test-utils" % "3.7.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "slf4j-simple" % "2.0.13"
)


