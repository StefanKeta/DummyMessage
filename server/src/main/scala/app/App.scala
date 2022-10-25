package app

import cats.effect.{ExitCode, IO, IOApp}
import server.EmberServer

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    httpApp <- EmberServer.defaultHttpApp[IO]
    exitCode <- EmberServer
      .server[IO](httpApp)
      .as(ExitCode.Success)
  } yield exitCode

}
