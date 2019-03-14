package io.github.pauljamescleary.petstore.domain.mailer

import cats.effect.{Async, IO}
import courier._
import io.github.pauljamescleary.petstore.config.MailerConfig

import scala.concurrent.{ExecutionContext, Future}
import org.log4s.getLogger

class MailerService[F[_]](mailerConfig: MailerConfig, petstoreBaseUrl:String)(implicit F: Async[F]) {
  import ExecutionContext.Implicits.global

  def logger = getLogger

  lazy val mailer = {
    var builder = Mailer(mailerConfig.host,mailerConfig.port)

    if (mailerConfig.user.nonEmpty) {
      builder = builder.as(mailerConfig.user, mailerConfig.password).auth(true)
    }

    if (mailerConfig.startTls.isDefined) {
      builder = builder.startTls(mailerConfig.startTls.get)
    }

    builder()
  }

  def send(envelope: Envelope): F[Unit] = {
    if (mailerConfig.mock) {
      logger.info("Sending email: " + envelope)
      Future.successful(())
      F.pure(())
    } else {
      F.liftIO(IO.fromFuture(IO.pure(mailer(envelope))))
    }
  }

  def mkResponseUrl(path:String): String = {
    if (path.startsWith("/"))
      petstoreBaseUrl + path
    else
      petstoreBaseUrl + "/" + path
  }
}

object MailerService {
  def apply[F[_]: Async](mailerConfig: MailerConfig, petstoreUrl:String) = new MailerService[F](mailerConfig,petstoreUrl)
}
