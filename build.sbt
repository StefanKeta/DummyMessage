ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

addCompilerPlugin(
  "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
)

val http4sV = "0.23.16"
val tapirV = "1.1.3"
val catsV = "2.8.0"
val catsEffectV = "3.3.14"

val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % catsV,
    "org.typelevel" %% "cats-effect" % catsEffectV
  ),
  addCompilerPlugin(
    "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

lazy val root = (project in file("."))
  .settings(
    name := "DummyMessage"
  )
  .aggregate(server, client, domain)

lazy val server = (project in file("server"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % http4sV,
      "org.http4s" %% "http4s-ember-server" % http4sV,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirV
    )
  )
  .dependsOn(domain, routes)

lazy val client = (project in file("client"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % http4sV,
      "org.http4s" %% "http4s-ember-client" % http4sV
    )
  )

lazy val routes = (project in file("routes"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % http4sV,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirV
    )
  )
  .dependsOn(domain)

lazy val domain = (project in file("domain"))
  .settings(
    commonSettings,
    libraryDependencies ++=
      Seq("com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV)
  )
