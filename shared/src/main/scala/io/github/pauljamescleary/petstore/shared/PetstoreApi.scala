package io.github.pauljamescleary.petstore.shared

import io.github.pauljamescleary.petstore.domain
import domain.{PetAlreadyExistsError, PetNotFoundError}
import domain.authentication._
import domain.pets.Pet
import domain.users.User
import shapeless.ops.hlist.Prepend
import typedapi.shared.{CompositionCons, HeaderListBuilder}


// Typed API
object PetstoreApi {

  import typedapi._

  val `allow-origin` = Headers.serverSend("Access-Control-Allow-Origin", "*")

  // JWT via Bearer schema
  val `Authorization` = Headers.client[String]("Authorization")

  // TODO Upstream should probably have this helper
  implicit class HeaderHelper[H1 <: shapeless.HList](hlb1: HeaderListBuilder[H1]) {
    import shapeless._

    def :|:[H2 <: HList](hlb2: HeaderListBuilder[H2])(implicit ev: Prepend[H1,H2]) : HeaderListBuilder[ev.Out] = {
      HeaderListBuilder()
    }
  }

  // TODO Upstream should probably have this helper
  implicit class ConsHelper[H1 <: shapeless.HList](hlb1: CompositionCons[H1]) {
    import shapeless._

    def ++[H2 <: HList](hlb2: CompositionCons[H2])(implicit ev: Prepend[H1,H2]) : CompositionCons[ev.Out] = {
      CompositionCons()
    }
  }


  val baseHeaders = `allow-origin`

  val unsecuredEp = baseHeaders

  val securedEpHeaders = baseHeaders :|: `Authorization`

  /*
  POST        /sign-in                    auth.controllers.SignInController.signIn
  GET         /sign-out                   auth.controllers.SignOutController.signOut

  POST        /account/register           auth.controllers.AccountController.register
  POST        /account/activation         auth.controllers.AccountController.send
  GET         /account/activation/:token  auth.controllers.AccountController.activate(token: java.util.UUID)

  POST        /password/recovery          auth.controllers.PasswordController.recover                           // re-send recovery email
  GET         /password/recovery/:token   auth.controllers.PasswordController.validate(token: java.util.UUID)   // only validates the token
  POST        /password/recovery/:token   auth.controllers.PasswordController.reset(token: java.util.UUID)      // allows password reset
  */

  private val authRts =
    // Sign In
    apiWithBody(
      method = Post[Json, SignInResponse],
      body = ReqBody[Json, SignInRequest],
      path = Root / "auth" / "sign-in",
      headers = baseHeaders) :|:   // server will include the Authorization header in the response
    // Sign Out
    api(
      method = Get[Json, Unit],
      path = Root / "auth" / "sign-out",
      headers = securedEpHeaders)


  private val accountRts =
    // Register user account
    apiWithBody(
      method = Post[Json, User],
      body = ReqBody[Json, RegistrationRequest],
      path = Root / "auth" / "account" / "register",
      headers = baseHeaders) :|:
    // (Re-)Send activation email
    apiWithBody(
      method = Post[Json, Unit],
      body = ReqBody[Json, Unit],
      path = Root / "auth" / "account" / "activation",
      headers = baseHeaders) :|:
    // Activate account
    api(
      method = Get[Json, User],
      path = Root / "auth" / "account" / "activation" / Segment[String]("token"),
      headers = baseHeaders)

  private val passwordRts =
    // Recover - sends recovery email
    apiWithBody(
      method = Post[Json, Unit],
      body = ReqBody[Json, Unit],
      path = Root / "auth" / "password" / "recovery",
      headers = baseHeaders) :|:
    // Validate recovery token
    api(
      method = Get[Json, Unit],
      path = Root / "auth" / "password" / "recovery" / Segment[String]("token"),
      headers = baseHeaders) :|:
    // Reset password
    apiWithBody(
      method = Post[Json, User],
      body = ReqBody[Json, PasswordResetRequest],
      path = Root / "auth" / "password" / "recovery" / Segment[String]("token"),
      headers = baseHeaders)


  val AuthApi = authRts ++ accountRts ++ passwordRts


  val PetsApi =
    // GET /pets?{pageSize:Int}&{offset:Int} => List[Pet] (list pets)
    api(
      method = Get[Json, List[Pet]],
      path = Root / "pets",
      queries = Queries.add[Int]('pageSize).add[Int]('offset),
      headers = securedEpHeaders
    ) :|:
    // POST {body:Pet} /pets => Pet (create)
    apiWithBody(
      method = Post[Json, Either[PetAlreadyExistsError,Pet]],
      body = ReqBody[Json, Pet],
      path = Root / "pets",
      headers = securedEpHeaders)  :|:
    // PUT {body:Pet} /pets/{id:Long} => Pet (update)
    apiWithBody(
      method = Put[Json, Either[PetNotFoundError.type,Pet]],
      body = ReqBody[Json, Pet],
      path = Root / "pets" / Segment[Long]("id"),
      headers = securedEpHeaders) :|:
    // DELETE /pets/{id:Long} => () (delete)
    api(
      method = Delete[Json, Unit],
      path = Root / "pets" / Segment[Long]("id"),
      headers = securedEpHeaders)
}

