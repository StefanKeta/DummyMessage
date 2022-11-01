package endpoints.user

import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._
import sttp.model.StatusCode
import io.circe.generic.auto._
import endpoints._
import domain.user._
import error.{ErrorInfo, InvalidEmail, UserAlreadyExists, ValidationErrors}

trait UserEndpoints {
  val registerUser: BaseEndpoint[User, ErrorInfo, StatusCode] =
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
      .out(statusCode)
}
