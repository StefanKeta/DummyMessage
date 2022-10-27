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

lazy val storage = createModule(
  "storage",
  Seq(
    "org.tpolecat" %% "skunk-core" % "0.3.2"
  )
)
  .dependsOn(core)

lazy val domain = createModule(
  "domain",
  Seq("com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV)
)

lazy val routes = createModule(
  "routes",
  Seq(
    "org.http4s" %% "http4s-core" % http4sV,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirV
  )
)
  .dependsOn(domain)

lazy val server = createModule(
  "server",
  Seq(
    "org.http4s" %% "http4s-core" % http4sV,
    "org.http4s" %% "http4s-ember-server" % http4sV,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirV
  )
)
  .dependsOn(core,domain, routes)

def createModule(name: String, extraDependencies: Seq[ModuleID]) = {
  Project(name,file(name))
    .settings(
      commonSettings,
      libraryDependencies ++= extraDependencies
    )
}
