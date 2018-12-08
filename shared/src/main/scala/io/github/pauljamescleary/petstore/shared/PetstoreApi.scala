package io.github.pauljamescleary.petstore.shared

import io.github.pauljamescleary.petstore.domain.authentication.LoginRequest
import io.github.pauljamescleary.petstore.shared.domain.users.User

trait PetstoreApi {
  // message of the day
  def welcomeMsg(name: String): String

  def signIn(creds:LoginRequest): Option[User]
}
