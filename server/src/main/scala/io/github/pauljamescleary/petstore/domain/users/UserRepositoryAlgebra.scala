package io.github.pauljamescleary.petstore.domain.users

import cats.MonadError
import cats.data.OptionT
import tsec.authentication.BackingStore


trait UserRepositoryAlgebra[F[_]] { outer =>
  def create(user: User): F[User]

  def update(user: User): F[Option[User]]

  def get(userId: Long): F[Option[User]]

  def delete(userId: Long): F[Option[User]]

  def findByUserName(userName: String): F[Option[User]]

  def findByEmail(email: String): F[Option[User]]

  def deleteByUserName(userName: String): F[Option[User]]

  def list(pageSize: Int, offset: Int): F[List[User]]

  def F: MonadError[F,Throwable]

  val userSecurityStore: BackingStore[F, Long, User] = new BackingStore[F, Long, User] {
    def get(id: Long): OptionT[F, User] = {
      OptionT(outer.get(id))
    }

    // Expects the element not to exist
    def put(elem: User): F[User] = {
      outer.create(elem)
    }

    // Update if it already exists, otherwise create. More like upsert.
    def update(user: User): F[User] = {
      F.flatMap(outer.update(user)) {
        case Some(u) => F.pure(u)
        case None => outer.create(user)
      }
    }

    def delete(id: Long): F[Unit] = {
      F.flatMap(outer.delete(id)) {
        case Some(_) => F.unit
        case None => F.raiseError(new IllegalArgumentException)
      }
    }
  }
}