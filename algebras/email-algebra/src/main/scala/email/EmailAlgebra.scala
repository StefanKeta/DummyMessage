package email

import cats.effect.kernel.{Async, Sync}
import cats.implicits._
import config.EmailConfig

import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{
  Authenticator,
  Message,
  PasswordAuthentication,
  Session,
  Transport
}

trait EmailAlgebra[F[_]] {
  def sendInviteEmail(
      emailConfig: EmailConfig,
      to: String,
      message: String
  ): F[Unit]
}

object EmailAlgebra {
  def instance[F[_]](implicit F: EmailAlgebra[F]): EmailAlgebra[F] = F
  def apply[F[_]: Async]: EmailAlgebra[F] = new EmailAlgebra[F] {
    override def sendInviteEmail(
        emailConfig: EmailConfig,
        to: String,
        token: String
    ): F[Unit] = for {
      session <- emailSession(config = emailConfig)
      _ <- sendEmail(
        session,
        emailConfig.sender,
        to,
        EmailTemplates.inviteTemplate(token)
      )
    } yield ()

    private def emailSession(config: EmailConfig): F[Session] = {
      Sync[F].delay {
        val properties = System.getProperties
        properties.put("mail.smtp.host", config.host)
        properties.put("mail.smtp.port", config.port)
        properties.put("mail.smtp.ssl.enable", "true")
        properties.put("mail.smtp.auth", "true")
        Session.getInstance(
          properties,
          new Authenticator {
            override def getPasswordAuthentication: PasswordAuthentication =
              new PasswordAuthentication(config.sender, config.password)
          }
        )
      }
    }

    private def sendEmail(
        session: Session,
        from: String,
        to: String,
        message: String
    ): F[Unit] = Sync[F].delay {
      val mimeMessage = new MimeMessage(session)
      mimeMessage.setFrom(new InternetAddress(from))
      mimeMessage.addRecipient(
        Message.RecipientType.TO,
        new InternetAddress(to)
      )
      mimeMessage.setSubject("Invite message subject")
      mimeMessage.setText(message)
      Transport.send(mimeMessage)
    }
  }
}
