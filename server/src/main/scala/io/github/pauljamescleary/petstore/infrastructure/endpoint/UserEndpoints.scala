package io.github.pauljamescleary.petstore
package infrastructure.endpoint

import cats.effect.Effect
import cats.implicits._
//import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

import scala.language.higherKinds
import domain._
import domain.users.{User, _}

class UserEndpoints[F[_]: Effect] extends Http4sDsl[F] {
  import Pagination._

  /* Jsonization of our User type */

  implicit val userDecoder: EntityDecoder[F, User] = jsonOf

  private def updateEndpoint(userService: UserService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ PUT -> Root / "users" / name =>
        val action = for {
          user <- req.as[User]
          updated = user.copy(userName = name)
          result <- userService.update(updated).value
        } yield result

        action.flatMap {
          case Right(saved) => Ok(saved.asJson)
          case Left(UserNotFoundError) => NotFound("User not found")
        }
    }

  private def listEndpoint(userService: UserService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "users" :? OptionalPageSizeMatcher(pageSize) :? OptionalOffsetMatcher(offset) =>
        for {
          retrieved <- userService.list(pageSize.getOrElse(10), offset.getOrElse(0))
          resp <- Ok(retrieved.asJson)
        } yield resp
    }

  private def searchByNameEndpoint(userService: UserService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "users" / userName =>
        userService.getUserByName(userName).value.flatMap {
          case Right(found) => Ok(found.asJson)
          case Left(UserNotFoundError) => NotFound("The user was not found")
        }
    }

  private def deleteUserEndpoint(userService: UserService[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case DELETE -> Root / "users" / userName =>
        for {
          _ <- userService.deleteByUserName(userName)
          resp <- Ok()
        } yield resp
    }

  def endpoints(userService: UserService[F]): HttpRoutes[F] =
    updateEndpoint(userService) <+>
    listEndpoint(userService)   <+>
    searchByNameEndpoint(userService)   <+>
    deleteUserEndpoint(userService)
}

object UserEndpoints {
  def endpoints[F[_]: Effect](
    userService: UserService[F]
  ): HttpRoutes[F] =
    new UserEndpoints[F].endpoints(userService)
}
