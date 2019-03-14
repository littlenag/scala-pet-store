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
  * Holds multiple kinds of secure tokens. These tokens are used for route authentication, account activation, and password recovery.
  *
  * @param id      Holds a SecureRandomId for route authentication, UUID-like string for account activation, UUID string for password recovery
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