package io.github.pauljamescleary.petstore.domain.crypt

import java.util.UUID

import scala.language.higherKinds
import io.github.pauljamescleary.petstore.domain.users.{User, UserRepositoryAlgebra}
import org.http4s.{Headers, HttpRoutes, Request, Response, Status}
import org.http4s.headers
import tsec.common.{SecureRandomId, VerificationStatus}
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.{BCrypt, JCAPasswordPlatform}
import cats._
import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import tsec.authentication.{TSecAuthService, _}
import tsec.authorization._
import tsec.mac.jca.{HMACSHA256, MacSigningKey}

import scala.collection.mutable
import scala.concurrent.duration._

abstract class AuthService[F[_]](implicit F: Sync[F]) {
  // Emphemeral type that let's us re-use different password hashers
  type PW

  import org.log4s.getLogger
  def logger = getLogger

  def Auth: SecuredRequestHandler[F, Long, User, AuthenticatedCookie[HMACSHA256, Long]]

  def tokenStore: BackingStore[F, UUID, AuthenticatedCookie[HMACSHA256, Long]]

  private def liftWithFallthrough(service: TSecAuthService[User, AuthenticatedCookie[HMACSHA256, Long], F])
                                 (implicit ME: MonadError[Kleisli[OptionT[F, ?], Request[F], ?], Throwable]): HttpRoutes[F] = {

    val middleware: TSecMiddleware[F, User, AuthenticatedCookie[HMACSHA256, Long]] = service => {
      Kleisli { r: Request[F] =>
        Kleisli(Auth.authenticator.extractAndValidate)
          .run(r)
          .flatMap(service.run)
      }
    }

    ME.handleErrorWith(middleware(service)) { e: Throwable =>
      logger.error(e)("Caught unhandled exception in authenticated service")
      Kleisli.liftF(OptionT.none)
    }
  }

  /**
    * Normal TSec helpers compose with an overly secure default. That is, they error out rather than trying subsequent routes. Thus we have to use our
    * own lifting mechanics to get somewhat more friendly behavior.
    *
    * The standard lifting functions can be accessed with the [[Auth]].
    *
    * @param pf   Route definition to lift.
    * @param ME   MonadError context to interpret in.
    * @return
    */
  def liftRoute(pf: PartialFunction[SecuredRequest[F, User, AuthenticatedCookie[HMACSHA256, Long]], F[Response[F]]])(implicit ME: MonadError[Kleisli[OptionT[F, ?], Request[F], ?], Throwable]): HttpRoutes[F] = {
    liftWithFallthrough(TSecAuthService(pf))
  }

  def coerceToPasswordHash(raw:String): PasswordHash[PW] = PasswordHash[PW](raw)

  def checkPassword(password:String, hash: PasswordHash[PW]): F[VerificationStatus]
  def hashPassword(password:String): F[PasswordHash[PW]]

  import AuthHelpers._

  //final val anyAuth: BasicRBAC[F, Role, User, Long] = BasicRBAC.all
  final val AdminRequired: BasicRBAC[F, Role, User, AuthenticatedCookie[HMACSHA256, Long]] = BasicRBAC(AuthHelpers.Role.Administrator)
  final val UserRequired: BasicRBAC[F, Role, User, AuthenticatedCookie[HMACSHA256, Long]] = BasicRBAC(AuthHelpers.Role.Administrator, AuthHelpers.Role.User)
}


class AuthServiceImpl[F[_]: Sync,A](jcaPasswordPlatform: JCAPasswordPlatform[A], userRepo: UserRepositoryAlgebra[F]) extends AuthService[F] {

  type PW = A

  import AuthHelpers._

  val tokenStore: BackingStore[F, UUID, AuthenticatedCookie[HMACSHA256, Long]] =
    inMemBackingStore[F, UUID, AuthenticatedCookie[HMACSHA256, Long]](_.id)

  val userStore: BackingStore[F, Long, User] = userRepo.userSecurityStore

  val settings: TSecCookieSettings = TSecCookieSettings(
    cookieName = "sps-auth-cookie",
    secure = false,              // https://www.owasp.org/index.php/SecureFlag
    httpOnly = true,
    expiryDuration = 30.minutes, // Absolute expiration time.
    maxIdle = None               // Rolling window expiration.
  )

  val key: MacSigningKey[HMACSHA256] = HMACSHA256.generateKey[Id]

  val tokenAuth =
    SignedCookieAuthenticator(
      settings,
      tokenStore,
      userStore,
      key
    )

  val Auth: SecuredRequestHandler[F, Long, User, AuthenticatedCookie[HMACSHA256, Long]] = SecuredRequestHandler(tokenAuth)

  private implicit val hasher: PasswordHasher[F,PW] = jcaPasswordPlatform.syncPasswordHasher[F]

  override def checkPassword(password:String, hash:PasswordHash[PW]): F[VerificationStatus] = {
    jcaPasswordPlatform.checkpw[F](password, hash)
  }

  override def hashPassword(password:String): F[PasswordHash[PW]]  = {
    jcaPasswordPlatform.hashpw(password)
  }
}

object AuthService {

  def bcrypt[F[_]: Sync](userRepo: UserRepositoryAlgebra[F]): AuthService[F] =
    new AuthServiceImpl(BCrypt,userRepo)
}

object AuthHelpers {

  /**
    *
    * @param getId
    * @param F
    * @tparam F
    * @tparam I   Id
    * @tparam V   User
    * @return
    */
  def inMemBackingStore[F[_], I, V](getId: V => I)(implicit F: Sync[F]) = new BackingStore[F, I, V] {
    private val storageMap = mutable.HashMap.empty[I, V]

    def put(v: V): F[V] = {
      val map = storageMap.put(getId(v), v)
      if (map.isEmpty)
        F.pure(v)
      else
        F.raiseError(new IllegalArgumentException)
    }

    def get(id: I): OptionT[F, V] =
      OptionT.fromOption[F](storageMap.get(id))

    def update(v: V): F[V] = {
      storageMap.update(getId(v), v)
      F.pure(v)
    }

    def delete(id: I): F[Unit] =
      storageMap.remove(id) match {
        case Some(_) => F.unit
        case None    => F.raiseError(new IllegalArgumentException)
      }
  }

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
