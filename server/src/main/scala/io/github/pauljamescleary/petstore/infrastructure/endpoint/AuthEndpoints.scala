package io.github.pauljamescleary.petstore
package infrastructure.endpoint

import cats.data.EitherT
import cats.effect.Effect
import cats.implicits._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

import scala.language.higherKinds
import domain._
import domain.users.{User, _}
import domain.authentication._
import io.github.pauljamescleary.petstore.domain.auth.{AuthHelpers, AuthService}
import tsec.authentication.TSecBearerToken
import tsec.common.Verified
import tsec.passwordhashers.PasswordHash
import tsec.authentication._

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
      AuthHelpers.Role.User.roleRepr,
      false                           // unactivated by default
    )
  }

  /* Jsonization of our User type */

  implicit val userDecoder: EntityDecoder[F, User] = jsonOf

  implicit val loginReqDecoder: EntityDecoder[F, SignInRequest] = jsonOf
  implicit val logoutReqDecoder: EntityDecoder[F, SignOutRequest] = jsonOf
  implicit val signupReqDecoder: EntityDecoder[F, RegistrationRequest] = jsonOf
  implicit val activationReqDecoder: EntityDecoder[F, ActivationEmailRequest] = jsonOf
  implicit val recoveryReqDecoder: EntityDecoder[F, PasswordRecoveryRequest] = jsonOf
  implicit val resetReqDecoder: EntityDecoder[F, PasswordResetRequest] = jsonOf

  private val signInEndpoint: HttpRoutes[F] =
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

  private val signOutEndpoint: HttpRoutes[F] =
    authService.liftRoute {
      case req @ POST -> Root / "auth" / "sign-out" asAuthed user =>
        for {
          // remove the web token
          signOutRequest <- req.request.as[SignOutRequest]
          _ <- authService.securedRequestHandler.authenticator.discard(req.authenticator)
          resp <- Ok(signOutRequest.userName)
        } yield resp
    }

  private val registerEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "auth" / "account" / "register" =>
        val action: EitherT[F, UserAlreadyExistsError, User] = for {
          signup <- EitherT.liftF(req.as[RegistrationRequest])
          hash <- EitherT.liftF(authService.hashPassword(signup.password))
          userSpec = signup.asUser(hash)
          user <- userService.createUser(userSpec)
          _ <- EitherT.liftF(authService.createActivationInfo(user))
          _ <- EitherT.liftF(authService.sendActivationEmail(user.email))
        } yield user

        action.value.flatMap {
          case Right(user) => Ok(user.asJson) // add auth token header
          case Left(UserAlreadyExistsError(existing)) => Conflict(s"Account with userName '${existing.userName}' already exists")
        }
    }

  private val activationEmailEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "auth" / "account" / "activation" =>
        for {
          activate <- req.as[ActivationEmailRequest]
          _ <- authService.sendActivationEmail(activate.email)
          resp <- Ok(activate.email)
        } yield resp
    }

  private val activateEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ GET -> Root / "auth" / "account" / "activation" / token =>
        for {
          _ <- authService.processActivationToken(token)
          resp <- Ok(token)
        } yield resp
    }

  // re-send password recovery email
  private val recoveryEmailEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "auth" / "password" / "recovery" =>
        for {
          recover <- req.as[PasswordRecoveryRequest]
          _ <- authService.sendRecoveryEmail(recover.email)
          resp <- Ok(recover.email)
        } yield resp
    }

  // validate the password reset token, used by the client to either prompt for a new password, or inform that is invalid/expired
  private val validateRecoveryTokenEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ GET -> Root / "auth" / "password" / "recovery" / token =>
        for {
          isValid <- authService.checkRecoveryToken(token)
          resp <- if (isValid) Ok(token) else UnprocessableEntity(token)  // include content to make firefox happy
        } yield resp
    }

  // reset a password
  private val resetPasswordEndpoint: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "auth" / "password" / "recovery" / token =>
        for {
          reset <- req.as[PasswordResetRequest]
          _ <- authService.processPasswordReset(token,reset)
          resp <- Ok(token)
        } yield resp
    }

  def endpoints: HttpRoutes[F] =
    signInEndpoint <+>
    signOutEndpoint <+>
    registerEndpoint <+>
    activationEmailEndpoint <+>
    activateEndpoint <+>
    recoveryEmailEndpoint <+>
    validateRecoveryTokenEndpoint <+>
    resetPasswordEndpoint
}

object AuthEndpoints {
  def endpoints[F[_]: Effect](userService: UserService[F], authService: AuthService[F]): HttpRoutes[F] =
    new AuthEndpoints[F](userService,authService).endpoints
}
