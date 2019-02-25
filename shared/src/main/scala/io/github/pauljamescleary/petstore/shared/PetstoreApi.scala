package io.github.pauljamescleary.petstore.shared

//import io.github.pauljamescleary.petstore.domain.UserAuthenticationFailedError
//import io.github.pauljamescleary.petstore.domain.authentication.LoginRequest
import io.github.pauljamescleary.petstore.domain.authentication.{LoginRequest, SignupRequest}
import io.github.pauljamescleary.petstore.domain.users.User


// Typed API
object PetstoreApi {

  import typedapi._

  val Api =
    // POST /login => User
    apiWithBody(
      method = Post[MT.`Application/json`, User],
      body = ReqBody[Json, LoginRequest],
      path = Root / "login") :|:
    // POST /users => User (signup)
    apiWithBody(
      method = Post[MT.`Application/json`, User],
      body = ReqBody[Json, SignupRequest],
      path = Root / "users")
    // GET {body: User} /fetch/user?{name: String}

}

