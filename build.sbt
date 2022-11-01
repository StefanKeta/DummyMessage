ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

addCompilerPlugin(
  "org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full
)

val http4sV = "0.23.16"
val tapirV = "1.1.3"
val catsV = "2.8.0"
val catsEffectV = "3.3.14"
lazy val doobieVersion = "1.0.0-RC1"

val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % catsV,
    "org.typelevel" %% "cats-effect" % catsEffectV,
    "ch.qos.logback" % "logback-classic" % "1.4.4"
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

lazy val client = (project in file("client"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % http4sV,
      "org.http4s" %% "http4s-ember-client" % http4sV
    )
  )

lazy val core = createModule(
  "core",
  Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.17.1",
    "org.flywaydb" % "flyway-core" % "9.6.0",
    "org.postgresql" % "postgresql" % "42.5.0"
  )
)

lazy val domain = createModule(
  "domain",
  Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres" % doobieVersion,
    "com.beachape" %% "enumeratum-doobie" % "1.6.0",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV,
    "com.beachape" %% "enumeratum-circe" % "1.7.0"
  )
)

lazy val endpoints = createModule(
  "endpoints",
  Seq(
    "org.http4s" %% "http4s-core" % http4sV,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirV,
    "com.beachape" %% "enumeratum-circe" % "1.7.0"
  )
)
  .dependsOn(domain)

lazy val routes = createModule(
  "routes",
  Seq(
    "org.http4s" %% "http4s-core" % http4sV,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirV,
    "com.beachape" %% "enumeratum-circe" % "1.7.0"
  )
)
  .dependsOn(domain, endpoints, userAlgebra)

lazy val server = createModule(
  "server",
  Seq(
    "org.http4s" %% "http4s-core" % http4sV,
    "org.http4s" %% "http4s-ember-server" % http4sV,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirV
  )
)
  .dependsOn(core, domain, routes)

lazy val storage = createModule(
  "storage",
  Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres" % doobieVersion,
    "com.beachape" %% "enumeratum-circe" % "1.7.0"
  )
)
  .dependsOn(core, domain)

lazy val util = createModule(
  "util",
  Seq(
    "de.mkammerer" % "argon2-jvm" % "2.11"
  )
)
  .dependsOn(domain)

lazy val userAlgebra = (project in file("algebras/user-algebra"))
  .settings(
    commonSettings
  )
  .dependsOn(domain, storage, util)

def createModule(name: String, extraDependencies: Seq[ModuleID] = Seq()) = {
  Project(name, file(name))
    .settings(
      commonSettings,
      libraryDependencies ++= extraDependencies
    )
}
