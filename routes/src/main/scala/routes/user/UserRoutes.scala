package routes.user

import cats.effect.Async
import domain.Executor
import endpoints.user.UserEndpoints
import sttp.tapir.server.http4s.Http4sServerInterpreter

class UserRoutes[F[_]: Async: Http4sServerInterpreter](
    executor: Executor[F]
) extends UserEndpoints {
   val routes = Http4sServerInterpreter[F].toRoutes(
    List(
      registerUser.serverLogicRecoverErrors(executor.register),
      activateUser.serverLogicRecoverErrors(executor.activate),
      loginUser.serverLogicRecoverErrors(executor.login)
    )
  )
  val userEndpoints = List(registerUser, activateUser)
}

object UserRoutes {
  def apply[F[_]: Async: Http4sServerInterpreter](
      executor: Executor[F]
  ) =
    new UserRoutes[F](executor)
}
