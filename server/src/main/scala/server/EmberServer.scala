package server

import cats.effect.Async
import cats.implicits._
import com.comcast.ip4s._
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import base._
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import user_routes.UserRoutes

object EmberServer {
  def defaultHttpApp[F[_]: Async]: F[HttpApp[F]] = for {
    userRoutes <- UserRoutes[F].pure[F]
    implicit0(http4sInterpreter: Http4sServerInterpreter[F]) <-
      Http4sServerInterpreter[F]().pure[F]
    endpoints <- (List(healthEndpoint) ++ userRoutes.userEndpoints).pure[F]
    routes <- (List(healthRoute[F]()) ++ userRoutes.userRoutes).pure[F]
    swaggerInterpreter <- toRoutes(
      SwaggerInterpreter().fromEndpoints[F](endpoints, "Title", "v1")
    ).pure[F]
    routesInterpreter <- toRoutes(routes).pure[F]
  } yield Router("/" -> (swaggerInterpreter <+> routesInterpreter)).orNotFound

  def server[F[_]: Async](httpApp: HttpApp[F]) = EmberServerBuilder
    .default[F]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(httpApp)
    .build
    .useForever
}
