ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

ThisBuild / scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) => Seq("-Ykind-projector:underscores")
    case Some((2, 12 | 13)) =>
      Seq("-Xsource:3", "-P:kind-projector:underscore-placeholders")
  }
}
val zioVersion = "2.1.6"

lazy val root = (project in file("."))
  .settings(
    name := "Zio2SimpleMusicStreamer"
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-http" % "0.0.5",
  "dev.zio" %% "zio-direct" % "1.0.0-RC7",
  "org.apache.tika" % "tika-core" % "2.9.2",
//  "org.apache.tika" % "tika-parsers" % "2.6.0" pomOnly(),
  "org.apache.tika" % "tika-parsers-standard-package" % "2.9.2",
  "ch.qos.logback" % "logback-classic" % "1.4.5" % Test,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
