package email

object EmailTemplates {
  val inviteTemplate = (token: String) =>
    s"Thank you for joining, please activate your account: https://www.dummy.com/activate/$token"
}
