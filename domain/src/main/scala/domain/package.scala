import java.util.UUID

package object domain {
  object MessageTypes{
    type MessageId = UUID
    type UserId = UUID
    type MessageText = String
  }

  object UserTypes{
    type UserId = UUID
    type FirstName = String
    type LastName = String
    type Email = String
    type Password = String
  }
}
