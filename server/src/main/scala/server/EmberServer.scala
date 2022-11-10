package server

import auth.AuthAlgebra
import cats.effect.{Async, Resource}
import cats.implicits._
import com.comcast.ip4s._
import config.AppConfig
import doobie.util.transactor.Transactor
import email.EmailAlgebra
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import password.PasswordHasher
import routes.ServerExecutor
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import routes.user.UserRoutes
import storage.{UserQueries, UserTokensQueries}
import time.Time
import token.SecureRandom
import user_algebra.UserAlgebra

object EmberServer {
  def defaultHttpApp[F[_]: Async](config: AppConfig): F[HttpApp[F]] = for {
    implicit0(http4sInterpreter: Http4sServerInterpreter[F]) <-
      Http4sServerInterpreter[F]().pure[F]
    implicit0(transactor: Transactor[F]) <-
      Transactor
        .fromDriverManager[F](
          "org.postgresql.Driver", // driver classname
          s"jdbc:postgresql:${config.db.name}", // connect URL (driver-specific)
          s"${config.db.username}", // user
          s"${config.db.password}" // password
        )
        .pure[F]
    implicit0(passwordHasher: PasswordHasher[F]) <- PasswordHasher[F].pure[F]
    implicit0(random: SecureRandom[F]) <- SecureRandom.instance[F]().pure[F]
    implicit0(time: Time[F]) <- Time.instance[F]().pure[F]
    userQueries <- UserQueries().pure[F]
    userTokensQueries <- UserTokensQueries().pure[F]
    userAlgebra <- UserAlgebra[F](userQueries, userTokensQueries).pure[F]
    emailAlgebra <- EmailAlgebra[F].pure[F]
    authAlgebra <- AuthAlgebra.instance(userQueries, userTokensQueries).pure[F]
    serverExecutor <- new ServerExecutor[F](
      appConfig = config,
      userAlgebra = userAlgebra,
      emailAlgebra = emailAlgebra,
      authAlgebra = authAlgebra
    ).pure[F]
    userRoutes <- UserRoutes[F](serverExecutor).pure[F]
    endpoints <- userRoutes.userEndpoints.pure[F]
    routes <- userRoutes.routes.pure[F]
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
