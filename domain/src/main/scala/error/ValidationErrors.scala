package error

sealed trait ValidationErrors extends ErrorInfo

case class InvalidEmail(errorMessage: String) extends ValidationErrors

case class InvalidPassword(errorMessage: String) extends ValidationErrors

case class InvalidFirstName(errorMessage: String) extends ValidationErrors

case class InvalidLastName(message: String) extends ValidationErrors
