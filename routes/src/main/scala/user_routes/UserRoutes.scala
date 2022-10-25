package user_routes

import base._
import cats.Applicative
import cats.implicits._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.statusCode
import user.User

class UserRoutes[F[_]:Applicative] {
  val registerUser: BaseEndpoint[User,StatusCode]=
    baseEndpoint.post
      .in("register")
      .in(jsonBody[User])
      .out(statusCode)

  private val registerRoute = registerUser.serverLogic(_ => StatusCode.Created.asRight[Unit].pure[F])
  val userEndpoints = List(registerUser)
  val userRoutes = List(registerRoute)
}

object UserRoutes{
  def apply[F[_]:Applicative] = new UserRoutes[F]
}
