package io.github.pauljamescleary.petstore.domain.auth

import cats._
import cats.data.EitherT
import cats.implicits._
import io.github.pauljamescleary.petstore.domain.UserNotFoundError

class AuthInfoValidationInterpreter[F[_]: Monad](authInfoRepo: AuthInfoRepositoryAlgebra[F]) extends AuthInfoValidationAlgebra[F] {
  def activationAuthExists(userId:Long): EitherT[F, Unit, AuthInfo] = EitherT {
    authInfoRepo.findByUserId(userId).map {
      case None => Left(())
      case Some(auth) => Right(auth)
    }
  }
}


