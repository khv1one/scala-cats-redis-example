import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9"

  lazy val http4sEndpointsServer = "org.endpoints4s" %% "http4s-server" % "7.0.0"
  lazy val http4sEndpointsClient = "org.endpoints4s"  %% "http4s-client" % "5.0.0"

  lazy val redisson = "org.redisson" % "redisson" % "3.16.2"

  lazy val endpoints = "org.endpoints4s" %% "algebra" % "1.5.0"
  lazy val endpointsCirce = "org.endpoints4s" %% "algebra-circe"  % "1.5.0"

  val cats = "org.typelevel" %% "cats-core" % "2.6.1"
  val catsEffect = "org.typelevel" %% "cats-effect" % "3.2.8"

  val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.16.0"

  val distageCore = "io.7mind.izumi" %% "distage-core" % "1.0.8"
  val distageFramework = "io.7mind.izumi" %% "distage-framework" % "1.0.8"

  val catsMtl                 = "org.typelevel"                 %% "cats-mtl-core"                  % "0.7.1"
  val catsTaglessMacros       = "org.typelevel"                 %% "cats-tagless-macros"            % "0.14.0"
  val catsTaglessCore         = "org.typelevel"                 %% "cats-tagless-core"              % "0.14.0"

  val fs2 = "co.fs2" %% "fs2-core" % "3.1.2"

  val circe                   = "io.circe" %% "circe-core" % "0.14.1"
  val circeGeneric            = "io.circe" %% "circe-generic" % "0.14.1"
  val circeGenericExtras      = "io.circe" %% "circe-generic-extras" % "0.14.1"
  val circeParser             = "io.circe" %% "circe-parser" % "0.14.1"
  val circeConfig             = "io.circe" %% "circe-config" % "0.8.0"

  val logCatsSlf4 = "org.typelevel" %% "log4cats-slf4j"   % "2.1.1"

}
