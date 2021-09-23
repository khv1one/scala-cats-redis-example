import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val common = project.in(file("base/common"))
  .enablePlugins(DockerPlugin)
  .settings(name := "Common")
  .settings(version := "0.1")
  .settings(options)
  .settings(
    libraryDependencies ++= Seq(logback, redisson, cats, catsEffect)
  )

lazy val serviceRpc = project.in(file("base/rpc"))
  .settings(name := "Service Rpc")
  .settings(version := "0.1")
  .settings(options)
  .settings(
    libraryDependencies ++= Seq(endpoints, endpointsCirce)
  )

lazy val service = project.in(file("service"))
  .enablePlugins(DockerPlugin)
  .settings(name := "Service API")
  .settings(version := "0.1")
  .settings(options)
  .settings(
    libraryDependencies ++= Seq(pureConfig, logCatsSlf4, http4sServer, http4sEndpointsServer)
  )
  .dependsOn(common, serviceRpc)

lazy val daemon = project.in(file("daemon"))
  .enablePlugins(DockerPlugin)
  .settings(name := "Redis Daemon")
  .settings(version := "0.1")
  .settings(options)
  .settings(
    libraryDependencies ++= Seq(pureConfig, fs2, logCatsSlf4)
  )
  .dependsOn(common)

val options = scalacOptions ++= Seq(
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:existentials",
  "-language:postfixOps",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-Yrangepos",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-encoding", "utf8"
)