package io.github.pauljamescleary.petstore.domain.auth

import java.time.Instant
import java.time.temporal.ChronoUnit

import scala.language.higherKinds
import io.github.pauljamescleary.petstore.domain.users.{User, UserRepositoryAlgebra}
import org.http4s.{HttpRoutes, Request, Response}
import tsec.common.VerificationStatus
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.{BCrypt, JCAPasswordPlatform}
import cats._
import cats.implicits._
import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import io.github.pauljamescleary.petstore.domain.authentication.PasswordResetRequest
import io.github.pauljamescleary.petstore.domain.mailer.MailerService
import tsec.authentication.{TSecAuthService, _}
import tsec.authorization._
import org.log4s.getLogger

import scala.concurrent.duration._
import scala.util.Random

class AuthService[F[_]](mailerService: MailerService[F], userRepo: UserRepositoryAlgebra[F], authInfoRepo: AuthInfoRepositoryAlgebra[F])(implicit F: Sync[F]) {

  type JCA = BCrypt
  val jcaPasswordPlatform: JCAPasswordPlatform[JCA] = BCrypt

  def logger = getLogger

  def generateActivationToken: F[String] = F.pure(Random.alphanumeric.take(20).mkString(""))

  def createActivationInfo(user:User): F[AuthInfo] = user.id match {
    case None =>
      F.raiseError(new IllegalArgumentException(s"User must have ID."))
    case Some(userId) =>
      for {
        secureId <- generateActivationToken
        authInfo = AuthInfo(secureId, userId, Instant.now().plus(3, ChronoUnit.DAYS), Option(Instant.now()), AuthInfoKind.Activation)
        created <- authInfoRepo.create(authInfo)
      } yield created
  }

  /**
    * Pre-conditions for the user:
    *   - exists
    *   - not yet active
    *   - has an activation token
    *
    * @param email
    * @return
    */
  def sendActivationEmail(email:String): F[Unit] = {
    import courier._

    val action: F[Unit] = for {
      maybeUser <- userRepo.findByEmail(email)
      user <- maybeUser match {
        case Some(user) if !user.activated => user.pure[F]
        case _ => F.raiseError[User](new IllegalArgumentException)
      }
      maybeAuthInfo <- authInfoRepo.findByUserId(user.id.get, AuthInfoKind.Activation.some)
      authInfo <- maybeAuthInfo match {
        case Some(authInfo) => authInfo.pure[F]
        case None => createActivationInfo(user)  // If the token was cleaned up, then just regen.
      }

      activationUrl = mailerService.mkResponseUrl("/auth/account/activation/" + authInfo.id)
      _ <- mailerService.send(
        Envelope.from("scala-pet-store" at "example.com")
        .to(user.email.addr)
        .subject("Welcome to the Scala Pet Store")
        .content(Text(s"""To activate your account visit <a href="$activationUrl">$activationUrl</a>"""))
      )

    } yield ()

    action orElse ().pure[F]
  }

  def processActivationToken(token:String): F[Unit] = {
    for {
      maybeAuthInfo <- authInfoRepo.get(token, AuthInfoKind.Activation.some)
      authInfo <- maybeAuthInfo match {
        case Some(authInfo) => authInfo.pure[F]
        case None => F.raiseError[AuthInfo](new IllegalArgumentException("Invalid token."))
      }

      maybeUser <- userRepo.get(authInfo.userId)
      user <- maybeUser match {
        case Some(user) if !user.activated => user.pure[F]
        case Some(user) if user.activated => F.raiseError[User](new IllegalArgumentException("User already activated but had activation token !?!"))
        case None => F.raiseError[User](new IllegalArgumentException("No user for valid activation token !?!"))
      }

      _ <- userRepo.update(user.copy(activated = true))

    } yield ()
  }

  ////

  def generateRecoveryToken: F[String] = F.pure(java.util.UUID.randomUUID().toString)

  def createRecoveryInfo(user:User): F[AuthInfo] = user.id match {
    case None =>
      F.raiseError(new IllegalArgumentException(s"User must have ID."))
    case Some(userId) =>
      for {
        secureId <- generateRecoveryToken
        authInfo = AuthInfo(secureId, userId, Instant.now().plus(2, ChronoUnit.HOURS), Option(Instant.now()), AuthInfoKind.Recovery)
        created <- authInfoRepo.create(authInfo)
      } yield created
  }


  /**
    * Pre-conditions for the user:
    *   - exists
    *   - must be active
    *   - has an activation token
    *
    * @param email
    * @return
    */
  def sendRecoveryEmail(email:String): F[Unit] = {
    import courier._

    val action: F[Unit] = for {
      maybeUser <- userRepo.findByEmail(email)
      user <- maybeUser match {
        case Some(user) if user.activated => user.pure[F]
        case _ => F.raiseError[User](new IllegalArgumentException)
      }

      // generate a new recovery token every time
      authInfo <- createRecoveryInfo(user)

      recoveryUrl = mailerService.mkResponseUrl("/#/password-reset/" + authInfo.id)
      _ <- mailerService.send(
        Envelope.from("scala-pet-store" at "example.com")
        .to(user.email.addr)
        .subject("Scala Pet Store - Password Reset")
        .content(Text(s"""To reset your password visit <a href="$recoveryUrl">$recoveryUrl</a>"""))
      )

    } yield ()

    action orElse ().pure[F]
  }

  def checkRecoveryToken(token:String): F[Unit] = {
    for {
      maybeAuthInfo <- authInfoRepo.get(token, AuthInfoKind.Recovery.some)
      resp <- maybeAuthInfo match {
        case Some(_) => ().pure[F]
        case None => F.raiseError[Unit](new IllegalArgumentException("Invalid token."))
      }
    } yield resp
  }

  def processPasswordReset(token:String, passwordReset: PasswordResetRequest): F[Unit] = {
    for {
      maybeAuthInfo <- authInfoRepo.get(token, AuthInfoKind.Recovery.some)
      authInfo <- maybeAuthInfo match {
        case Some(authInfo) => authInfo.pure[F]
        case None => F.raiseError[AuthInfo](new IllegalArgumentException("Invalid token."))
      }

      maybeUser <- userRepo.get(authInfo.userId)
      user <- maybeUser match {
        case Some(user) if user.activated => user.pure[F]
        case Some(user) if !user.activated => F.raiseError[User](new IllegalArgumentException("User not activated but needs password reset !?!"))
        case None => F.raiseError[User](new IllegalArgumentException("No user for valid recovery token !?!"))
      }

      hashed <- hashPassword(passwordReset.newPassword)
      _ <- userRepo.update(user.copy(hash = hashed.toString))

    } yield ()
  }


  /**
    * Normal TSec helpers compose with an overly secure default. That is, they error out rather than trying subsequent routes. Thus we have to use our
    * own lifting mechanics to get somewhat more friendly behavior.
    *
    * The standard lifting functions can be accessed with the [[securedRequestHandler]].
    *
    * @param pf   Route definition to lift.
    * @param ME   MonadError context to interpret in.
    * @return
    */
  def liftRoute(pf: PartialFunction[SecuredRequest[F, User, TSecBearerToken[Long]], F[Response[F]]])(implicit ME: MonadError[Kleisli[OptionT[F, ?], Request[F], ?], Throwable]): HttpRoutes[F] = {
    liftWithPushthrough(TSecAuthService(pf))
  }

  private def liftWithPushthrough(service: TSecAuthService[User, TSecBearerToken[Long], F])
                                 (implicit ME: MonadError[Kleisli[OptionT[F, ?], Request[F], ?], Throwable]): HttpRoutes[F] = {

    val middleware: TSecMiddleware[F, User, TSecBearerToken[Long]] = service => {
      Kleisli { r: Request[F] =>
        Kleisli(securedRequestHandler.authenticator.extractAndValidate)
          .run(r)
          .flatMap(service.run)
      }
    }

    ME.handleErrorWith(middleware(service)) { e: Throwable =>
      logger.error(e)("Caught unhandled exception in authenticated service")
      Kleisli.liftF(OptionT.none)
    }
  }

  private val authInfoStore = authInfoRepo.authInfoStore

  private val userStore = userRepo.userSecurityStore

  private val settings =
    TSecTokenSettings(
      expiryDuration = 30.minutes, // Absolute expiration time
      maxIdle = None
    )

  private val bearerTokenAuth =
    BearerTokenAuthenticator(
      authInfoStore,
      userStore,
      settings
    )

  val securedRequestHandler: SecuredRequestHandler[F, Long, User, TSecBearerToken[Long]] = SecuredRequestHandler(bearerTokenAuth)

  private implicit def hasher: PasswordHasher[F, JCA] = jcaPasswordPlatform.syncPasswordHasher[F]

  def coerceToPasswordHash(raw:String): PasswordHash[JCA] = PasswordHash[JCA](raw)

  def checkPassword(password:String, hash:PasswordHash[JCA]): F[VerificationStatus] = {
    jcaPasswordPlatform.checkpw[F](password, hash)
  }

  def hashPassword(password:String): F[PasswordHash[JCA]]  = {
    jcaPasswordPlatform.hashpw(password)
  }

  // In case we ever want RBAC...
  //import AuthHelpers._
  //final val AdminRequired: BasicRBAC[F, Role, User, TSecBearerToken[Long]] = BasicRBAC(AuthHelpers.Role.Administrator)
  //final val UserRequired: BasicRBAC[F, Role, User, TSecBearerToken[Long]] = BasicRBAC(AuthHelpers.Role.Administrator, AuthHelpers.Role.User)
}

object AuthService {
  def apply[F[_]: Sync](mailerService: MailerService[F], userRepo: UserRepositoryAlgebra[F], authInfoRepo: AuthInfoRepositoryAlgebra[F]): AuthService[F] =
    new AuthService(mailerService,userRepo,authInfoRepo)
}

object AuthHelpers {

  /*
   * In our example, we will demonstrate how to use SimpleAuthEnum, as well as Role based authorization
   */
  sealed case class Role(roleRepr: String)

  import cats.implicits._

  object Role extends SimpleAuthEnum[Role, String] {

    val Administrator = Role("Administrator")
    val User          = Role("User")

    implicit val E: Eq[Role] = Eq.fromUniversalEquals[Role]

    def getRepr(t: Role): String = t.roleRepr

    protected val values: AuthGroup[Role] = AuthGroup(Administrator, User)
  }

  implicit def authRole[F[_]](implicit F: MonadError[F, Throwable]): AuthorizationInfo[F, Role, User] =
    new AuthorizationInfo[F, Role, User] {
      def fetchInfo(u: User): F[Role] = Role.fromReprF[F](u.role)
    }
}


