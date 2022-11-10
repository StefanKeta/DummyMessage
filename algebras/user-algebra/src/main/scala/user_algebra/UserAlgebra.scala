package user_algebra
import cats.effect.Sync
import domain.user.User
import doobie.util.transactor.Transactor
import password.PasswordHasher
import storage.{UserQueries, UserTokensQueries}
import time.Time
import token.SecureRandom
import user_algebra.impl.UserAlgebraImpl

trait UserAlgebra[F[_]] {
  def registerUser(user: User): F[String] //Returning token for registration
  def activateUser(token: String): F[Unit]
}

object UserAlgebra {
  def apply[F[_]: Sync: Transactor: PasswordHasher: SecureRandom: Time](
      userQueries: UserQueries,
      userTokensQueries: UserTokensQueries
  ) =
    new UserAlgebraImpl[F](userQueries, userTokensQueries)
}
