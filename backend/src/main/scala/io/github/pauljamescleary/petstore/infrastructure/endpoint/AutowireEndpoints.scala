package io.github.pauljamescleary.petstore.infrastructure.endpoint

import java.nio.ByteBuffer

import boopickle.Default._
import cats.effect.{ContextShift, Effect, IO}
import org.http4s.dsl.Http4sDsl
import cats.data.{NonEmptyList, OptionT}
import io.github.pauljamescleary.petstore.domain.authentication.LoginRequest
import io.github.pauljamescleary.petstore.domain.orders.OrderService
import io.github.pauljamescleary.petstore.domain.users.UserService
import io.github.pauljamescleary.petstore.shared.PetstoreApi
import io.github.pauljamescleary.petstore.shared.domain.users.User
import org.http4s.CacheDirective.`no-cache`
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{`Cache-Control`, `Content-Type`}
import org.log4s.getLogger
import org.scalatest.path
import scalatags.Text.TypedTag

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

/**
  *
  */
class AutowireEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  private[this] val logger = getLogger

  private[this] val F = implicitly[Effect[F]]

  /* Need Instant Json Encoding */
  import io.circe.java8.time._

  /* Needed for service composition via |+| */
  import cats.implicits._

  def endpoints(apiService: ApiService[F])(implicit ec:ExecutionContext): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> "autowire" /: path =>
        // get the request body as Array[Byte]
        req.as[Array[Byte]].flatMap { b =>
          // call Autowire route
          val x =
            F.liftIO(
              IO.fromFuture[ByteBuffer](
                IO(Router.route[PetstoreApi](apiService)(
                  autowire.Core.Request(path.toString.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(ByteBuffer.wrap(b)))
                ))
              )
            )

          x.flatMap { buffer =>
            val data = Array.ofDim[Byte](buffer.remaining())
            buffer.get(data)
            Ok(data)
          }
        }
    }}

object AutowireEndpoints {
  def endpoints[F[_]: Effect](userService: UserService[F], orderService: OrderService[F])(implicit ec:ExecutionContext): HttpRoutes[F] =
    new AutowireEndpoints[F].endpoints(new ApiService(userService, orderService))
}

object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle.apply[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}

class ApiService[F[_]: Effect](userService: UserService[F], orderService: OrderService[F]) extends PetstoreApi {
  override def logIn(creds: LoginRequest): Either[String, User] = Left("Not Implemented")

  override def logOut(): Either[String, Unit] = Left("Not implemented")
}
