package routes

import auth.AuthAlgebra
import cats.effect._
import cats.effect.implicits.genSpawnOps
import domain.Executor
import cats.implicits._
import config.AppConfig
import domain.user.{AuthCtx, BasicCredentials, User}
import email.EmailAlgebra
import user_algebra.UserAlgebra

class ServerExecutor[F[_]: Async](
    appConfig: AppConfig,
    userAlgebra: UserAlgebra[F],
    emailAlgebra: EmailAlgebra[F],
    authAlgebra: AuthAlgebra[F]
) extends Executor[F] {
  override def register(user: User): F[Unit] = for {
    tokenToSend <- userAlgebra
      .registerUser(user)
    fiber <- emailAlgebra
      .sendInviteEmail(
        appConfig.email,
        user.email,
        tokenToSend
      )
      .start
    result <- fiber.join
  } yield result match {
    case Outcome.Succeeded(fa) => Async[F].unit
    case Outcome.Errored(e)    => Async[F].raiseError(e)
    case Outcome.Canceled()    => Async[F].unit
  }

  override def activate(token: String): F[Unit] = for {
    _ <- userAlgebra.activateUser(token)
  } yield ()

  override def login(basicCredentials: BasicCredentials): F[AuthCtx] = for{
    ctx <- authAlgebra.authenticateUser(basicCredentials)
  } yield ctx
}
