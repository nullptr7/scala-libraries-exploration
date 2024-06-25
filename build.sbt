ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "scala-libraries-exploration"
  )

libraryDependencies ++= Seq(
  "eu.timepit"                   %% "refined"                 % "0.11.1",
  "eu.timepit"                   %% "refined-cats"            % "0.11.2",
  "io.circe"                     %% "circe-core"              % "0.14.7",
  "io.circe"                     %% "circe-parser"            % "0.14.7",
  "io.circe"                     %% "circe-generic"           % "0.14.7",
  "com.fasterxml.jackson.core"    % "jackson-databind"        % "2.17.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala"    % "2.17.0",
  "org.http4s"                   %% "http4s-dsl"              % "0.23.18",
  "org.http4s"                   %% "http4s-circe"            % "0.23.18",
  "org.http4s"                   %% "http4s-blaze-server"     % "0.23.14",
  "org.http4s"                   %% "http4s-blaze-client"     % "0.23.14",
  "dev.zio"                      %% "zio-streams"             % "2.1.3",
  "io.d11"                       %% "zhttp"                   % "2.0.0-RC11",
  "org.parboiled"                %% "parboiled"               % "2.5.1",
  "com.typesafe.play"            %% "play"                    % "2.9.3",
  "io.estatico"                  %% "newtype"                 % "0.4.4",
  "org.apache.httpcomponents"     % "httpclient"              % "4.5.14",
  "com.softwaremill.sttp.tapir"  %% "tapir-zio"               % "1.7.3",
  "com.softwaremill.sttp.tapir"  %% "tapir-zio-http-server"   % "1.10.8",
  "com.softwaremill.sttp.tapir"  %% "tapir-swagger-ui-bundle" % "1.10.7",
  "com.softwaremill.sttp.tapir"  %% "tapir-json-zio"          % "1.10.8",
  "org.mockito"                  %% "mockito-scala"           % "1.17.22" % Test,
  "org.scalactic"                %% "scalactic"               % "3.2.16",
  "org.scalatest"                %% "scalatest"               % "3.2.16"  % Test,
  "org.slf4j"                     % "slf4j-api"               % "2.0.12",
  "org.slf4j"                     % "slf4j-simple"            % "2.0.13",
  "com.github.cb372"             %% "cats-retry"              % "3.1.3",
  "com.github.scopt"             %% "scopt"                   % "4.1.0",
  "org.rogach"                   %% "scallop"                 % "5.1.0",
)
