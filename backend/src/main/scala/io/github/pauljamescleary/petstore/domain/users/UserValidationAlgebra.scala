package io.github.pauljamescleary.petstore.domain.users

import scala.language.higherKinds
import cats.data.EitherT
import io.github.pauljamescleary.petstore.domain.{UserAlreadyExistsError, UserNotFoundError}
import io.github.pauljamescleary.petstore.shared.domain.users.User

trait UserValidationAlgebra[F[_]] {

  def doesNotExist(user: User): EitherT[F, UserAlreadyExistsError, Unit]

  def exists(userId: Option[Long]): EitherT[F, UserNotFoundError.type, Unit]
}
