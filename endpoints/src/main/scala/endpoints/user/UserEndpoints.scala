package endpoints.user

import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._
import sttp.model.StatusCode
import io.circe.generic.auto._
import endpoints._
import domain.user._
import error._

trait UserEndpoints {
  val registerUser: BaseEndpoint[User, ErrorInfo, Unit] =
    baseEndpoint.post
      .in("register")
      .in(jsonBody[User])
      .errorOut(
        oneOf[ErrorInfo](
          oneOfVariant(
            statusCode(StatusCode.BadRequest).and(jsonBody[UserAlreadyExists])
          ),
          oneOfVariant(
            statusCode(StatusCode.BadRequest).and(jsonBody[ValidationErrors])
          )
        )
      )
      .out(statusCode(StatusCode.Ok))

  val activateUser: BaseEndpoint[String, RegistrationErrors, Unit] =
    baseEndpoint.post
      .in("activate" / path[String])
      .errorOut(
        oneOf[RegistrationErrors](
          oneOfVariant(
            statusCode(StatusCode.BadRequest).and(jsonBody[TokenDoesNotExist])
          )
        )
      )

  val loginUser: BaseEndpoint[BasicCredentials, ErrorInfo, AuthCtx] =
    baseEndpoint.post
      .in("login")
      .in(jsonBody[BasicCredentials])
      .errorOut(
        oneOf[ErrorInfo](
          oneOfVariant[ErrorInfo](
            statusCode(StatusCode.BadRequest).and(jsonBody[ValidationErrors])
          ),
          oneOfVariant(
            statusCode(StatusCode.Unauthorized).and(jsonBody[UnauthorizedError])
          )
        )
      )
      .out(jsonBody[AuthCtx] and statusCode(StatusCode.Ok))
}
