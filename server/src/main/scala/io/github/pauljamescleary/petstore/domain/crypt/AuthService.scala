package io.github.pauljamescleary.petstore.domain.crypt

import scala.language.higherKinds
import io.github.pauljamescleary.petstore.domain.crypt.AuthService.UserAuthService
import io.github.pauljamescleary.petstore.domain.users.{User, UserRepositoryAlgebra, UserService}
import org.http4s.Response
import tsec.common.VerificationStatus
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.{BCrypt, JCAPasswordPlatform}
import cats._
import cats.data.OptionT
import cats.effect.Sync
import tsec.authentication.{TSecAuthService, _}
import tsec.authorization._
import tsec.common.SecureRandomId

import scala.collection.mutable
import scala.concurrent.duration._

abstract class AuthService[F[_],AuthInfo] {
  // Emphemeral type that let's us re-use different password hashers
  type PW

  def middleware: SecuredRequestHandler[F, Long, User, TSecBearerToken[Long]]

//  def forRoute2(pf: PartialFunction[AuthedRequest[F, AuthInfo], F[Response[F]]])(implicit F: Applicative[F]) = {
//    val x:AuthedService[AuthInfo, F] = Kleisli(req => pf.andThen(OptionT.liftF(_)).applyOrElse(req, Function.const(OptionT.none)))
//    middleware(x)
//  }

  def forRoute(pf: PartialFunction[SecuredRequest[F, User, TSecBearerToken[Long]], F[Response[F]]])(implicit F: Monad[F]) = {
    val x: TSecAuthService[User, TSecBearerToken[Long], F] = TSecAuthService.apply(pf)
    middleware.liftService(x)
  }

  def lift(h:String): PasswordHash[PW] = PasswordHash[PW](h)

  def checkpw(password:String, hash: PasswordHash[PW]): F[VerificationStatus]
  def hashpw(password:String): F[PasswordHash[PW]]
}


class AuthServiceImpl[F[_]: Sync,A](jcaPasswordPlatform: JCAPasswordPlatform[A], userRepo: UserRepositoryAlgebra[F]) extends UserAuthService[F] {
  import org.http4s.headers.Authorization

  type PW = A

  import AuthHelpers._ // import dummyBackingStore factory

  val bearerTokenStore: BackingStore[F, SecureRandomId, TSecBearerToken[Long]] = dummyBackingStore[F, SecureRandomId, TSecBearerToken[Long]](s => SecureRandomId.coerce(s.id))

  //We create a way to store our users. You can attach this to say, your doobie accessor
  //val userStore: BackingStore[F, Int, User] = backingStore[F, Int, User](_.id)

  val settings: TSecTokenSettings = TSecTokenSettings(
    expiryDuration = 30.minutes, //Absolute expiration time
    maxIdle = None
  )

  val bearerTokenAuth: BearerTokenAuthenticator[F, Long, User] =
    BearerTokenAuthenticator(
      bearerTokenStore,
      userRepo.userSecurityStore,
      settings
    )

  val middleware: SecuredRequestHandler[F, Long, User, TSecBearerToken[Long]] = SecuredRequestHandler(bearerTokenAuth)

  /*
  val authUser: Kleisli[OptionT[F, ?], Request[F], User] = Kleisli({ request =>
    val message = for {
      header <- OptionT.fromOption(request.headers.get(Authorization))
      token <- crypto.validateSignedToken(header.value).toRight("Invalid token")
      message <- Either.catchOnly[NumberFormatException](token.toLong).leftMap(_.toString)
    } yield message
    message.traverse(retrieveUser.run)
  })


  override val middleware: AuthMiddleware[F, User] = AuthMiddleware(authUser)
  */

  //import jcaPasswordPlatform._

  private implicit val hasher: PasswordHasher[F,PW] = jcaPasswordPlatform.syncPasswordHasher[F]

  def checkpw(password:String, hash:PasswordHash[PW]): F[VerificationStatus] = {
    //implicit val localFunctor: Functor[F] = ev
    jcaPasswordPlatform.checkpw[F](password, hash)
  }

  def hashpw(password:String): F[PasswordHash[PW]]  = {
    jcaPasswordPlatform.hashpw(password)
  }
}

object AuthService {
  type UserAuthService[F[_]] = AuthService[F,User]

  def bcrypt[F[_]: Sync](userRepo: UserRepositoryAlgebra[F]): UserAuthService[F] =
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
  def dummyBackingStore[F[_], I, V](getId: V => I)(implicit F: Sync[F]) = new BackingStore[F, I, V] {
    private val storageMap = mutable.HashMap.empty[I, V]

    def put(elem: V): F[V] = {
      val map = storageMap.put(getId(elem), elem)
      if (map.isEmpty)
        F.pure(elem)
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

    val Administrator: Role = Role("Administrator")
    val User: Role          = Role("User")

    implicit val E: Eq[Role] = Eq.fromUniversalEquals[Role]

    def getRepr(t: Role): String = t.roleRepr

    protected val values: AuthGroup[Role] = AuthGroup(Administrator, User)
  }

  implicit def authRole[F[_]](implicit F: MonadError[F, Throwable]): AuthorizationInfo[F, Role, User] =
    new AuthorizationInfo[F, Role, User] {
      def fetchInfo(u: User): F[Role] = Role.fromReprF[F](u.role)
    }

}
