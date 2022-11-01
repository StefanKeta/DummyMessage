package server

import cats.effect.Async
import cats.implicits._
import com.comcast.ip4s._
import doobie.util.transactor.Transactor
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import routes.ServerExecutor
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import routes.user.UserRoutes
import storage.UserQueries
import user_algebra.UserAlgebra

object EmberServer {
  def defaultHttpApp[F[_]: Async]: F[HttpApp[F]] = for {
    implicit0(http4sInterpreter: Http4sServerInterpreter[F]) <-
      Http4sServerInterpreter[F]().pure[F]
    implicit0(transactor: Transactor[F]) <- Transactor
      .fromDriverManager[F](
        "org.postgresql.Driver", // driver classname
        "jdbc:postgresql:dummy_message", // connect URL (driver-specific)
        "user", // user
        "password" // password
      )
      .pure[F]
    userQueries <- UserQueries().pure[F]
    userAlgebra <- UserAlgebra[F](userQueries).pure[F]
    serverExecutor <- new ServerExecutor[F](userAlgebra).pure[F]
    userRoutes <- UserRoutes[F](serverExecutor).pure[F]
    endpoints <- userRoutes.userEndpoints.pure[F]
    routes <- userRoutes.userRoutes.pure[F]
    swaggerInterpreter <- Http4sServerInterpreter[F]()
      .toRoutes(
        SwaggerInterpreter().fromEndpoints[F](endpoints, "Title", "v1")
      )
      .pure[F]
  } yield Router("/" -> (swaggerInterpreter <+> routes)).orNotFound

  def server[F[_]: Async](httpApp: HttpApp[F]) = EmberServerBuilder
    .default[F]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(httpApp)
    .build
    .useForever
}
