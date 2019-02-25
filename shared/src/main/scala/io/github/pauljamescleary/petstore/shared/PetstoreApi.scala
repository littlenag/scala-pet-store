package io.github.pauljamescleary.petstore.shared

//import io.github.pauljamescleary.petstore.domain.UserAuthenticationFailedError
//import io.github.pauljamescleary.petstore.domain.authentication.LoginRequest
import io.github.pauljamescleary.petstore.domain.PetAlreadyExistsError
import io.github.pauljamescleary.petstore.domain.authentication.{LoginRequest, SignupRequest}
import io.github.pauljamescleary.petstore.domain.pets.Pet
import io.github.pauljamescleary.petstore.domain.users.User


// Typed API
object PetstoreApi {

  import typedapi._

  val UsersApi =
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


  val PetsApi =
    // GET /pets?{pageSize:Int}&{offset:Int} => List[Pet] (list pets)
    api(
      method = Get[MT.`Application/json`, List[Pet]],
      path = Root / "pets",
      queries = Queries.add[Int]('pageSize).add[Int]('offset)
    ) :|:
    // POST {body:Pet} /pets => Pet (create)
    apiWithBody(
        method = Post[MT.`Application/json`, Either[PetAlreadyExistsError,Pet]],
        body = ReqBody[Json, Pet],
        path = Root / "pets")


}

