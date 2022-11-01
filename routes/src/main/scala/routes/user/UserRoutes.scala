package routes.user

import cats.effect.Async
import domain.Executor
import endpoints.user.UserEndpoints
import sttp.tapir.server.http4s.Http4sServerInterpreter

class UserRoutes[F[_]: Async: Http4sServerInterpreter](
    executor: Executor[F]
) extends UserEndpoints {
  private val registerRoute = Http4sServerInterpreter[F].toRoutes(
    registerUser.serverLogicRecoverErrors(executor.register)
  )
  val userEndpoints = List(registerUser)
  val userRoutes =
    registerRoute
}

object UserRoutes {
  def apply[F[_]: Async: Http4sServerInterpreter](
      executor: Executor[F]
  ) =
    new UserRoutes[F](executor)
}
