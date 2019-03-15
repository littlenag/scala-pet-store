package io.github.pauljamescleary.petstore.infrastructure.repository.doobie

import java.time.Instant

import cats._
import cats.data.OptionT
import cats.implicits._
import doobie._
import doobie.implicits._
import io.github.pauljamescleary.petstore.domain.auth.{AuthInfo, AuthInfoKind, AuthInfoRepositoryAlgebra}

private object AuthInfoSQL {

  /* We require type StatusMeta to handle our ADT Status */
  implicit val AuthInfoKindMeta: Meta[AuthInfoKind] =
    Meta[String].imap(AuthInfoKind.withName)(_.entryName)

  /* We require conversion for date time */
  implicit val DateTimeMeta: Meta[Instant] =
    Meta[java.sql.Timestamp].imap(_.toInstant)(java.sql.Timestamp.from _)

  def insert(authInfo: AuthInfo): Update0 = sql"""
    INSERT INTO AUTH_INFO (ID, USER_ID, EXPIRY, LAST_TOUCHED, KIND)
    VALUES (${authInfo.id}, ${authInfo.userId}, ${authInfo.expiry}, ${authInfo.lastTouched}, ${authInfo.kind})
  """.update

  def update(authInfo: AuthInfo): Update0 = sql"""
    UPDATE AUTH_INFO
    SET EXPIRY = ${authInfo.expiry}, LAST_TOUCHED = ${authInfo.lastTouched}
    WHERE ID = ${authInfo.id}
  """.update

  def select(id: String, kind: Option[AuthInfoKind]): Query0[AuthInfo] = {
    (sql"""
    SELECT ID, USER_ID, EXPIRY, LAST_TOUCHED, KIND
    FROM AUTH_INFO """ ++ Fragments.whereAndOpt(Some(fr"ID = $id"), kind.map(k => fr"KIND = $k"))).query
  }

  def delete(id: String, kind: Option[AuthInfoKind]): Update0 = {
    (sql"""DELETE FROM AUTH_INFO """ ++ Fragments.whereAndOpt(Some(fr"ID = $id"), kind.map(k => fr"KIND = $k"))).update
  }

  def selectByUserId(userId: Long, kind: Option[AuthInfoKind] = None): Query0[AuthInfo] =
    (sql"""
    SELECT ID, USER_ID, EXPIRY, LAST_TOUCHED, KIND
    FROM AUTH_INFO """ ++ Fragments.whereAndOpt(Some(fr"USER_ID = $userId"), kind.map(k => fr"KIND = $k"))).query
}

class DoobieAuthInfoRepositoryInterpreter[F[_]](val xa: Transactor[F])(implicit ev: MonadError[F, Throwable])
  extends AuthInfoRepositoryAlgebra[F] {

  import AuthInfoSQL._

  def create(authInfo: AuthInfo): F[AuthInfo] =
    insert(authInfo).run.transact(xa).as(authInfo)

  def update(authInfo: AuthInfo): F[AuthInfo] = AuthInfoSQL.update(authInfo).run.transact(xa).as(authInfo)

  def get(id: String, kind: Option[AuthInfoKind] = None): F[Option[AuthInfo]] = select(id,kind).option.transact(xa)

  def delete(id: String, kind: Option[AuthInfoKind] = None): F[Option[AuthInfo]] = OptionT(get(id,kind)).semiflatMap(user =>
    AuthInfoSQL.delete(id,kind).run.transact(xa).as(user)
  ).value

  def findByUserId(userId:Long, kind: Option[AuthInfoKind] = None): F[Option[AuthInfo]] = selectByUserId(userId,kind).option.transact(xa)

  override def ME: MonadError[F, Throwable] = ev
}

object DoobieAuthInfoRepositoryInterpreter {
  def apply[F[_]](xa: Transactor[F])(implicit ev: MonadError[F, Throwable]): DoobieAuthInfoRepositoryInterpreter[F] =
    new DoobieAuthInfoRepositoryInterpreter(xa)
}

