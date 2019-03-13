package io.github.pauljamescleary.petstore.domain.auth

import cats.implicits._
import cats.MonadError
import cats.data.OptionT
import tsec.authentication.{BackingStore, TSecBearerToken}
import tsec.common.SecureRandomId


trait AuthInfoRepositoryAlgebra[F[_]] { outer =>
  def create(user: AuthInfo): F[AuthInfo]

  def update(user: AuthInfo): F[AuthInfo]

  def get(id: String, kind: Option[AuthInfoKind] = None): F[Option[AuthInfo]]

  def delete(id: String, kind: Option[AuthInfoKind] = None): F[Option[AuthInfo]]

  def ME: MonadError[F,Throwable]

  val authInfoStore: BackingStore[F, SecureRandomId, TSecBearerToken[Long]] = new BackingStore[F, SecureRandomId, TSecBearerToken[Long]] {
    def get(id: SecureRandomId): OptionT[F, TSecBearerToken[Long]] = {
      OptionT(ME.map(outer.get(id,AuthInfoKind.Authentication.some))(_.flatMap {
        case authInfo if authInfo.kind == AuthInfoKind.Authentication => authInfo.asBearerToken.some
        case _ => none
      }))
    }

    // Expects the element not to exist
    def put(elem: TSecBearerToken[Long]): F[TSecBearerToken[Long]] = {
      ME.flatMap(outer.create(AuthInfo(elem))) {
        case authInfo if authInfo.kind == AuthInfoKind.Authentication => ME.pure(authInfo.asBearerToken)
        case _ => ME.raiseError(new IllegalArgumentException)
      }
    }

    // Update if it already exists, otherwise create. More like upsert.
    def update(user: TSecBearerToken[Long]): F[TSecBearerToken[Long]] = {
      ME.flatMap(outer.update(AuthInfo(user))){
        case authInfo if authInfo.kind == AuthInfoKind.Authentication => ME.pure(authInfo.asBearerToken)
        case _ => ME.raiseError(new IllegalArgumentException)
      }
    }

    def delete(id: SecureRandomId): F[Unit] = {
      ME.flatMap(outer.delete(id,AuthInfoKind.Authentication.some)) {
        case Some(_) => ME.unit
        case None => ME.raiseError(new IllegalArgumentException)
      }
    }
  }
}