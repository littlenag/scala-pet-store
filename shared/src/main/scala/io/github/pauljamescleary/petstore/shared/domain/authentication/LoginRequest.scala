package io.github.pauljamescleary.petstore.domain.authentication

final case class LoginRequest(
                                 userName: String,
                                 password: String
                             )

final case class SignupRequest(
                                  userName: String,
                                  firstName: String,
                                  lastName: String,
                                  email: String,
                                  password: String,
                                  phone: String,
                              )