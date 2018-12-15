package io.github.pauljamescleary.petstore.domain.authentication

import io.github.pauljamescleary.petstore.shared.JsonSerializers._

final case class LoginRequest(
                                 userName: String,
                                 password: String
                             )

object LoginRequest {
  implicit val decodeLoginReq = deriveDecoder[LoginRequest]
  implicit val encodeLoginReq = deriveEncoder[LoginRequest]
}

final case class SignupRequest(
                                  userName: String,
                                  firstName: String,
                                  lastName: String,
                                  email: String,
                                  password: String,
                                  phone: String,
                              )