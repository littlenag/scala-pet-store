package io.github.pauljamescleary.petstore.domain.authentication

import io.github.pauljamescleary.petstore.shared.JsonSerializers._

final case class SignInRequest(
                                 userName: String,
                                 password: String
                             )

object SignInRequest {
  implicit val decodeLoginReq = deriveDecoder[SignInRequest]
  implicit val encodeLoginReq = deriveEncoder[SignInRequest]
}

abstract class AuthToken(val value:String)

final case class AuthTokenOnly(override val value: String) extends AuthToken(value)

final case class SignOutRequest(
                                userName: String,
                                jwtToken:String
                              ) extends AuthToken(jwtToken)

object SignOutRequest {
  implicit val decodeLogoutReq = deriveDecoder[SignOutRequest]
  implicit val encodeLogoutReq = deriveEncoder[SignOutRequest]
}

final case class RegistrationRequest(
                                      userName: String,
                                      firstName: String,
                                      lastName: String,
                                      email: String,
                                      password: String,
                                      phone: String,
                                    )

object RegistrationRequest {
  implicit val decodeSignupReq = deriveDecoder[RegistrationRequest]
  implicit val encodeSignupReq = deriveEncoder[RegistrationRequest]
}

final case class PasswordRecoveryRequest(
                                          email: String
                                        )

object PasswordRecoveryRequest {
  implicit val decodePwdRvyReq = deriveDecoder[PasswordRecoveryRequest]
  implicit val encodePwdRvyReq = deriveEncoder[PasswordRecoveryRequest]
}

final case class PasswordResetRequest(
                                       newPassword: String,
                                       token:String
                                     )

object PasswordResetRequest {
  implicit val decodePwdResetReq = deriveDecoder[PasswordResetRequest]
  implicit val encodePwdResetReq = deriveEncoder[PasswordResetRequest]
}