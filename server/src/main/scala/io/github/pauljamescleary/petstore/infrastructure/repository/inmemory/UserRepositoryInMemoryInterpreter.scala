package io.github.pauljamescleary.petstore.infrastructure.repository.inmemory

import java.util.Random

import cats.implicits._
import cats.{Applicative, MonadError}
import io.github.pauljamescleary.petstore.domain.users.{User, UserRepositoryAlgebra}

import scala.collection.concurrent.TrieMap

class UserRepositoryInMemoryInterpreter[F[_]](implicit ev: MonadError[F, Throwable]) extends UserRepositoryAlgebra[F] {

  private val cache = new TrieMap[Long, User]

  private val random = new Random

  def create(user: User): F[User] = {
    val id = random.nextLong
    val toSave = user.copy(id = id.some)
    cache += (id -> toSave)
    toSave.pure[F]
  }

  def update(user: User): F[Option[User]] = user.id.traverse{ id =>
    cache.update(id, user)
    user.pure[F]
  }

  def get(id: Long): F[Option[User]] = cache.get(id).pure[F]

  def delete(id: Long): F[Option[User]] = cache.remove(id).pure[F]

  def findByUserName(userName: String): F[Option[User]] =
    cache.values.find(u => u.userName == userName).pure[F]

  def findByEmail(email: String): F[Option[User]] =
    cache.values.find(u => u.email == email).pure[F]

  def list(pageSize: Int, offset: Int): F[List[User]] =
    cache.values.toList.sortBy(_.lastName).slice(offset, offset + pageSize).pure[F]

  def deleteByUserName(userName: String): F[Option[User]] = {
    val deleted = for {
      user <- cache.values.find(u => u.userName == userName)
      removed <- cache.remove(user.id.get)
    } yield removed
    deleted.pure[F]
  }

  override def F: MonadError[F, Throwable] = ev
}

object UserRepositoryInMemoryInterpreter {
  def apply[F[_]: Applicative]()(implicit ev: MonadError[F, Throwable]) = new UserRepositoryInMemoryInterpreter[F]
}
