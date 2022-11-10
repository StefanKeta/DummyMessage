package domain.user

import java.util.UUID

case class AuthedUser(
    userId: UUID,
    email: String
)
