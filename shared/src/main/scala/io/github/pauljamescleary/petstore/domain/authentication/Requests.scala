package io.github.pauljamescleary.petstore.domain.authentication

import io.github.pauljamescleary.petstore.domain.users.User
import io.github.pauljamescleary.petstore.shared.JsonSerializers._

final case class SignInRequest(
                                 userName: String,
                                 password: String
                             )

object SignInRequest {
  implicit val decodeSignInReq = deriveDecoder[SignInRequest]
  implicit val encodeSignInReq = deriveEncoder[SignInRequest]
}

final case class AuthToken(value:String)

object AuthToken {
  implicit val decodeAuthToken = deriveDecoder[AuthToken]
  implicit val encodeAuthToken = deriveEncoder[AuthToken]
}

final case class SignInResponse(
                                 user: User,
                                 auth: AuthToken
                               )

object SignInResponse {
  implicit val decodeSignInRsp = deriveDecoder[SignInResponse]
  implicit val encodeSignInRsp = deriveEncoder[SignInResponse]
}

final case class SignOutRequest(
                                 userName: String,
                                 authToken: AuthToken
                               )

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

final case class ActivationEmailRequest(email: String)

object ActivationEmailRequest {
  implicit val decodeSignupReq = deriveDecoder[ActivationEmailRequest]
  implicit val encodeSignupReq = deriveEncoder[ActivationEmailRequest]
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