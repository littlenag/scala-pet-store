package io.github.pauljamescleary.petstore
package infrastructure.endpoint

import java.time.Instant

import cats.data.EitherT
import cats.effect.Effect
import cats.implicits._
//import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

import scala.language.higherKinds
import domain._
import domain.users.{User, _}
import domain.authentication._
import io.github.pauljamescleary.petstore.domain.crypt.AuthHelpers
import io.github.pauljamescleary.petstore.domain.crypt.AuthService
import tsec.authentication.TSecBearerToken
import tsec.common.{SecureRandomId, Verified}
import tsec.passwordhashers.PasswordHash

class AuthEndpoints[F[_]: Effect](userService: UserService[F], authService: AuthService[F]) extends Http4sDsl[F] {

  implicit class RichSignUp(signupRequest: RegistrationRequest) {
    import signupRequest._

    // Create User from the SignupRequest
    def asUser[PA](hashedPassword: PasswordHash[PA]): User = User(
      userName,
      firstName,
      lastName,
      email,
      hashedPassword.toString,
      phone,
      AuthHelpers.Role.User.roleRepr
    )
  }

  /*

  POST        /sign-in                    auth.controllers.SignInController.signIn
  GET         /sign-out                   auth.controllers.SignOutController.signOut

  POST        /register                   auth.controllers.RegistrationController.register

  POST        /account/activation         auth.controllers.AccountController.send
  GET         /account/activation/:token  auth.controllers.AccountController.activate(token: java.util.UUID)

  POST        /password/recovery          auth.controllers.PasswordController.recover                           // re-send recovery email
  GET         /password/recovery/:token   auth.controllers.PasswordController.validate(token: java.util.UUID)   // only validates the token
  POST        /password/recovery/:token   auth.controllers.PasswordController.reset(token: java.util.UUID)      // allows password reset

  */

  /* Jsonization of our User type */

  implicit val userDecoder: EntityDecoder[F, User] = jsonOf

  implicit val loginReqDecoder: EntityDecoder[F, SignInRequest] = jsonOf
  implicit val logoutReqDecoder: EntityDecoder[F, SignOutRequest] = jsonOf
  implicit val signupReqDecoder: EntityDecoder[F, RegistrationRequest] = jsonOf

  private def signInEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "auth" / "sign-in" =>
        val action: EitherT[F, UserAuthenticationFailedError, (SignInResponse,TSecBearerToken[Long])] = for {
          login <- EitherT.liftF(req.as[SignInRequest])
          name = login.userName
          user <- userService.getUserByName(name).leftMap(_ => UserAuthenticationFailedError(name))
          checkResult <- EitherT.liftF(authService.checkPassword(login.password, authService.coerceToPasswordHash(user.hash)))
          user <-
            if(checkResult == Verified) EitherT.rightT[F, UserAuthenticationFailedError](user)
            else EitherT.leftT[F, User](UserAuthenticationFailedError(name))

          // Update the auth token
          token <- EitherT.liftF(authService.securedRequestHandler.authenticator.create(user.id.get))

        } yield {
          (SignInResponse(user,AuthToken(token.id)),token)
        }

        action.value.flatMap {
          case Right((resp,token)) => Ok(resp.asJson).map(r => authService.securedRequestHandler.authenticator.embed(r,token))
          case Left(UserAuthenticationFailedError(name)) => BadRequest(s"Authentication failed for user $name")
        }
    }

  private def signOutEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "auth" / "sign-out" =>
        val action = for {
          // remove the web token
          logout <- req.as[SignOutRequest]
          result <- userService.signOut(logout).value
        } yield result

        action.flatMap {
          case Right(_) => Ok()
          case Left(UserTokenNotFoundError) => Conflict(s"The token was not found.")
        }
    }

  private def registerEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "auth" / "register" =>
        val action: EitherT[F, UserAlreadyExistsError, User] = for {
          signup <- EitherT.liftF(req.as[RegistrationRequest])
          hash <- EitherT.liftF(authService.hashPassword(signup.password))
          userSpec = signup.asUser(hash)
          user <- userService.createUser(userSpec)

          // Create the auth token
          //_ <- EitherT.liftF(authService.bearerTokenStore.delete())
          secureRandomId = SecureRandomId.Strong.generate
          bearerToken = TSecBearerToken(secureRandomId, user.id.get, Instant.now().plusSeconds(60 * 30), Option(Instant.now()))
          _ <- EitherT.liftF(authService.bearerTokenStore.put(bearerToken))

        } yield user

        action.value.flatMap {
          case Right(user) => Ok(user.asJson) // add auth token header
          case Left(UserAlreadyExistsError(existing)) => Conflict(s"The user with user name ${existing.userName} already exists")
        }
    }

  def endpoints: HttpRoutes[F] =
    signInEndpoint <+>
      signOutEndpoint <+>
      registerEndpoint

}

object AuthEndpoints {
  def endpoints[F[_]: Effect](userService: UserService[F], authService: AuthService[F]): HttpRoutes[F] =
    new AuthEndpoints[F](userService,authService).endpoints
}
