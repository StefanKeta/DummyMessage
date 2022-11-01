package error

sealed trait RegistrationErrors extends ErrorInfo

case class UnauthorizedError(message: String) extends RegistrationErrors
case class UserAlreadyExists(message: String) extends RegistrationErrors
