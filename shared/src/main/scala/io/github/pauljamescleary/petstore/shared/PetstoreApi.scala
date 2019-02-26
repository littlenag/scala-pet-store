package io.github.pauljamescleary.petstore.shared

import io.github.pauljamescleary.petstore.domain
import domain.PetAlreadyExistsError
import domain.authentication.{LoginRequest, SignupRequest}
import domain.pets.Pet
import domain.users.User


// Typed API
object PetstoreApi {

  import typedapi._

  val `allow-origin` = Headers.serverSend("Access-Control-Allow-Origin", "*")

  val UsersApi =
    // POST /login => User
    apiWithBody(
      method = Post[Json, User],
      body = ReqBody[Json, LoginRequest],
      path = Root / "login",
      headers = `allow-origin`) :|:
    // POST /users => User (signup)
    apiWithBody(
      method = Post[Json, User],
      body = ReqBody[Json, SignupRequest],
      path = Root / "users",
      headers = `allow-origin`)
    // GET {body: User} /fetch/user?{name: String}


  val PetsApi =
    // GET /pets?{pageSize:Int}&{offset:Int} => List[Pet] (list pets)
    api(
      method = Get[Json, List[Pet]],
      path = Root / "pets",
      queries = Queries.add[Int]('pageSize).add[Int]('offset),
      headers = `allow-origin`
    ) :|:
    // POST {body:Pet} /pets => Pet (create)
    apiWithBody(
      method = Post[Json, Either[PetAlreadyExistsError,Pet]],
      body = ReqBody[Json, Pet],
      path = Root / "pets",
      headers = `allow-origin`)
}

