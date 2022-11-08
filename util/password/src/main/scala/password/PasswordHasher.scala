package password

import cats.effect.kernel.Sync
import cats.implicits._
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Factory.Argon2Types
import error.FailedToHashPassword

trait PasswordHasher[F[_]] {
  def hashPassword(password: String): F[String]
}

object PasswordHasher {
  def apply[F[_]](implicit F: Sync[F]): PasswordHasher[F] =
    (password: String) => {
      F.delay {
        Argon2Factory
          .create(Argon2Types.ARGON2id)
          .hash(4, 1024 * 1024, 8, password.toCharArray)
      }.handleErrorWith(err =>
        F.raiseError(FailedToHashPassword(err.getMessage))
      )
    }
}
