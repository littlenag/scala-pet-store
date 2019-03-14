package io.github.pauljamescleary.petstore.domain.auth

import cats.data.EitherT
import io.github.pauljamescleary.petstore.domain.{UserAlreadyExistsError, UserNotFoundError}

import scala.language.higherKinds

trait AuthInfoValidationAlgebra[F[_]] {

  def activationAuthExists(userId:Long): EitherT[F, Unit, AuthInfo]

}
