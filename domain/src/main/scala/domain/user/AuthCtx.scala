package domain.user

case class AuthCtx(
    token: String,
    user: AuthedUser
)
