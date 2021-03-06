package io.github.pauljamescleary.petstore.infrastructure.repository.inmemory

import cats.MonadError
import cats.implicits._
import io.github.pauljamescleary.petstore.domain.auth.{AuthInfo, AuthInfoKind, AuthInfoRepositoryAlgebra}

import scala.collection.concurrent.TrieMap

class AuthInfoRepositoryInMemoryInterpreter[F[_]](implicit ev: MonadError[F, Throwable]) extends AuthInfoRepositoryAlgebra[F] {

  private val cache = new TrieMap[String, AuthInfo]

  def create(authInfo: AuthInfo): F[AuthInfo] = {
    cache += (authInfo.id -> authInfo)
    authInfo.pure[F]
  }

  def update(authInfo: AuthInfo): F[AuthInfo] = {
    cache.update(authInfo.id, authInfo)
    authInfo.pure[F]
  }

  def get(id: String, kind: Option[AuthInfoKind]): F[Option[AuthInfo]] = cache.get(id).pure[F]

  def delete(id: String, kind: Option[AuthInfoKind]): F[Option[AuthInfo]] = cache.remove(id).pure[F]

  def findByUserId(userId:Long, kind: Option[AuthInfoKind] = None): F[Option[AuthInfo]] =
    cache.values.find(u => u.userId == userId && Option(u.kind) == kind).pure[F]

  override def ME: MonadError[F, Throwable] = ev
}