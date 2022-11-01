package user_algebra
import cats.effect.Sync
import domain.user.User
import doobie.util.transactor.Transactor
import storage.UserQueries
import user_algebra.impl.UserAlgebraImpl

trait UserAlgebra[F[_]] {
  def registerUser(user: User): F[Int]
}

object UserAlgebra {
  def apply[F[_]: Sync: Transactor](userQueries: UserQueries) =
    new UserAlgebraImpl[F](userQueries)
}