package domain.user

import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try
case class User(
    firstName: String,
    lastName: String,
    gender: Gender,
    dob: LocalDate,
    email: String,
    password: String
)

object User {
  implicit val codec: Codec[User] = deriveCodec[User]
//  implicit val date: Codec[LocalDate] = {
//    val dateFormat: DateTimeFormatter =
//      DateTimeFormatter.ofPattern("yyyy-MM-dd")
//    val decoder: Decoder[LocalDate] =
//      Decoder.decodeString.emapTry(dateString =>
//        Try(LocalDate.parse(dateString, dateFormat))
//      )
//    val encoder: Encoder[LocalDate] =
//      Encoder.encodeString.contramap(date => date.format(dateFormat))
//    Codec.from(decoder, encoder)
//  }
  implicit val schema: Schema[User] = Schema.derived[User]
}
