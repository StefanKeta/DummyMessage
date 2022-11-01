package routes

import cats.effect.Sync
import domain.Executor
import cats.implicits._
import domain.user.User
import sttp.model.StatusCode
import user_algebra.UserAlgebra

class ServerExecutor[F[_]: Sync](userAlgebra: UserAlgebra[F])
    extends Executor[F] {
  override def register(user: User): F[StatusCode] = for {
    _ <- userAlgebra
      .registerUser(user)
  } yield StatusCode.Ok

}
