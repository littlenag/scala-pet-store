package io.github.pauljamescleary.petstore.domain.auth

import java.time.Instant

import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

import enumeratum._

sealed trait AuthInfoKind extends EnumEntry

case object AuthInfoKind extends Enum[AuthInfoKind] with CirceEnum[AuthInfoKind] {
  case object Authentication extends AuthInfoKind
  case object Activation extends AuthInfoKind
  case object Recovery extends AuthInfoKind

  val values = findValues
}


/**
  * Holds activation and authentication info.
  *
  *
  * @param id      Holds a SecureRandomId for authentication, UUID-like string for activation
  * @param userId
  * @param expiry
  * @param lastTouched
  */
case class AuthInfo(
                     id: String,
                     userId: Long,
                     expiry: Instant,
                     lastTouched: Option[Instant],
                     kind: AuthInfoKind
                   ) {
  lazy val asBearerToken: TSecBearerToken[Long] = TSecBearerToken(SecureRandomId.coerce(id),userId,expiry,lastTouched)
}

object AuthInfo {
  def apply(t: TSecBearerToken[Long]): AuthInfo = AuthInfo(t.id,t.identity,t.expiry,t.lastTouched,AuthInfoKind.Authentication)
}