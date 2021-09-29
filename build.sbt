import Dependencies._

ThisBuild / scalaVersion := "2.13.6"

lazy val common = project.in(file("base/common"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(options)
  .settings(
    name := "Common",
    version := "0.1",
    libraryDependencies ++= Seq(logback, redisson, cats, catsEffect),
    Universal / packageName := "common"
  )

lazy val serviceRpc = project.in(file("base/rpc"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(options)
  .settings(
    name := "Service Rpc",
    version := "0.1",
    libraryDependencies ++= Seq(circe, circeGeneric, circeGenericExtras, endpoints, endpointsCirce),
    Universal / packageName := "rpc"
  )
  .enablePlugins(JavaAppPackaging)

lazy val service = project.in(file("service"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(options)
  .settings(
    name := "Service API",
    version := "0.1",
    libraryDependencies ++= Seq(pureConfig, logCatsSlf4, http4sServer, http4sEndpointsServer),
    Universal / packageName := "service"
  )
  .dependsOn(common, serviceRpc)

lazy val daemon = project.in(file("daemon"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(options)
  .settings(
    name := "Redis Daemon",
    version := "0.1",
    libraryDependencies ++= Seq(pureConfig, fs2, logCatsSlf4, http4sClient, http4sEndpointsClient),
    Universal / packageName := "daemon"
  )
  .dependsOn(common, serviceRpc)

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