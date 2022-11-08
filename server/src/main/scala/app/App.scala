package app

import cats.effect.{ExitCode, IO, IOApp}
import config.AppConfig
import db.Migrator
import server.EmberServer

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    config <- AppConfig.load[IO]()
    _ <- Migrator.migrate[IO](config.db)
    httpApp <- EmberServer.defaultHttpApp[IO](config)
    exitCode <- EmberServer
      .server[IO](httpApp)
      .as(ExitCode.Success)
  } yield exitCode

}
