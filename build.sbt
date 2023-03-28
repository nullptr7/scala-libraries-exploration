ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "scala-libraries-exploration"
  )

libraryDependencies ++= Seq(
  "eu.timepit"                   %% "refined"              % "0.10.2",
  "eu.timepit"                   %% "refined-cats"         % "0.10.2",
  "io.circe"                     %% "circe-core"           % "0.14.5",
  "io.circe"                     %% "circe-parser"         % "0.14.5",
  "io.circe"                     %% "circe-generic"        % "0.14.5",
  "com.fasterxml.jackson.core"    % "jackson-databind"     % "2.14.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.2",
  "org.http4s"                   %% "http4s-dsl"           % "0.23.18",
  "org.http4s"                   %% "http4s-circe"         % "0.23.18",
  "org.http4s"                   %% "http4s-blaze-server"  % "0.23.14",
  "org.http4s"                   %% "http4s-blaze-client"  % "0.23.14",
)
