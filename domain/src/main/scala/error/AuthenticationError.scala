package error

sealed trait RegistrationErrors extends ErrorInfo

case class UnauthorizedError(message: String) extends RegistrationErrors
case class TokenDoesNotExist(message: String) extends RegistrationErrors
case class TokenExpired(message: String) extends RegistrationErrors
case class UserAlreadyActivated(message: String) extends RegistrationErrors
case class UserAlreadyExists(message: String) extends RegistrationErrors
case class UserDoesNotExist(message: String) extends RegistrationErrors
