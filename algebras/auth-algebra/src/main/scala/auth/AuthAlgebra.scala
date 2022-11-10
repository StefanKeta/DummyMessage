package auth

import cats.effect.{Spawn, Sync}
import cats.implicits._
import dao.{UserDao, UserTokenDao}
import domain.user.{AuthCtx, AuthedUser, BasicCredentials, User}
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import error.UserDoesNotExist
import password.PasswordHasher
import storage.{UserQueries, UserTokensQueries}
import time.Time
import token.SecureRandom

trait AuthAlgebra[F[_]] {
  def authenticateUser(basicCredentials: BasicCredentials): F[AuthCtx]
}

object AuthAlgebra {
  def apply[F[_]](implicit F: AuthAlgebra[F]): AuthAlgebra[F] = F
  def instance[F[_]: Sync](
      userQueries: UserQueries,
      userTokensQueries: UserTokensQueries
  )(implicit
      xa: Transactor[F],
      secureRandom: SecureRandom[F],
      passwordHasher: PasswordHasher[F],
      time: Time[F]
  ) = new AuthAlgebra[F] {
    override def authenticateUser(
        basicCredentials: BasicCredentials
    ): F[AuthCtx] = for {
      maybeUserDao <- userQueries
        .findByEmail(basicCredentials.email)
        .transact(xa)
      authCtx <- maybeUserDao match {
        case Some(userDao) =>
          for {
            passwordsMatch <- passwordHasher.verifyPassword(
              userDao.password,
              basicCredentials.password
            )
            _ <-
              if (passwordsMatch) Sync[F].unit
              else
                Sync[F].raiseError(
                  UserDoesNotExist("Invalid username and/or password")
                )
            authCtx <- provideAuthContext(userDao)
          } yield authCtx
        case None =>
          Sync[F].raiseError(
            UserDoesNotExist(
              "Invalid username and/or password"
            )
          )
      }
    } yield authCtx

    private def provideAuthContext(userDao: UserDao): F[AuthCtx] = for {
      token <- secureRandom.generateToken()
      user = AuthedUser(
        userId = userDao.uuid,
        email = userDao.email
      )
      tomorrow <- time.tomorrow
      _ <- userTokensQueries
        .insert(UserTokenDao(token, user.userId, tomorrow))
        .transact(xa)
    } yield AuthCtx(token, user)
  }
}
