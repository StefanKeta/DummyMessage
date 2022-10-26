package client

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.http4sLiteralsSyntax

object ClientApp extends IOApp{

  override def run(args: List[String]): IO[ExitCode] = EmberClientBuilder
    .default[IO]
    .build
    .use{ client =>
      client
        .expect[String](uri"http://localhost:8080/api/health")
        .flatMap(IO.println(_))
    }
    .as(ExitCode.Success)
}
