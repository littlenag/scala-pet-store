package io.github.pauljamescleary.petstore.shared

import io.github.pauljamescleary.petstore.domain.UserAuthenticationFailedError
import io.github.pauljamescleary.petstore.domain.authentication.LoginRequest
import io.github.pauljamescleary.petstore.domain.users.User

// Autowire API
trait PetstoreApi {
  def logIn(creds:LoginRequest): Either[UserAuthenticationFailedError, User]
  def logOut(): Either[String,Unit]


}
