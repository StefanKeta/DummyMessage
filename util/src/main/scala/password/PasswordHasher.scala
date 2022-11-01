package password

import cats.{Monad, MonadThrow}
import cats.implicits._
import de.mkammerer.argon2.{Argon2, Argon2Factory}
import de.mkammerer.argon2.Argon2Factory.Argon2Types
import error.FailedToHashPassword

trait PasswordHasher[F[_]] {
  def hashPassword(password: String): F[String]
}

object PasswordHasher {
  def apply[F[_]](implicit F: MonadThrow[F]) = new PasswordHasher[F] {
    override def hashPassword(password: String): F[String] = for {
      argon2id <- Argon2Factory.create(Argon2Types.ARGON2id).pure[F]
      hash <- argon2id.hash(4, 1024 * 1024, 8, password.toCharArray).pure[F]
      _ =
        if (argon2id.verify(hash, password.toCharArray))
          F.raiseError(FailedToHashPassword())
    } yield hash
  }
}
