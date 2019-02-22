package io.github.pauljamescleary.petstore.domain.crypt

import scala.language.higherKinds
import cats.effect.Sync
import tsec.common.VerificationStatus
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.{BCrypt, JCAPasswordPlatform}

abstract class CryptService[F[_]: Sync] {
  type T

  def lift(h:String): PasswordHash[T] = PasswordHash[T](h)

  def checkpw(password:String, hash: PasswordHash[T]): F[VerificationStatus]
  def hashpw(password:String): F[PasswordHash[T]]
}

class CryptServiceImpl[F[_]: Sync,A](jcaPasswordPlatform: JCAPasswordPlatform[A]) extends CryptService[F] {
  type T = A

  //import jcaPasswordPlatform._

  private implicit val hasher: PasswordHasher[F,T] = jcaPasswordPlatform.syncPasswordHasher[F]

  def checkpw(password:String, hash:PasswordHash[T]): F[VerificationStatus]  = {
    jcaPasswordPlatform.checkpw(password, hash)
  }

  def hashpw(password:String): F[PasswordHash[T]]  = {
    jcaPasswordPlatform.hashpw(password)
  }
}

object CryptService {
  def bcrypt[F[_]: Sync](): CryptService[F] =
    new CryptServiceImpl(BCrypt)
}

