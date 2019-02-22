package io.github.pauljamescleary.petstore.domain.users

import cats._
import cats.data._
import cats.effect.Sync
import cats.syntax.functor._
import io.github.pauljamescleary.petstore.domain.authentication.LoginRequest
import io.github.pauljamescleary.petstore.domain.crypt.CryptService
import io.github.pauljamescleary.petstore.domain.{UserAlreadyExistsError, UserAuthenticationFailedError, UserNotFoundError}
import tsec.common.Verified

class UserService[F[_]: Monad: Sync](userRepo: UserRepositoryAlgebra[F], validation: UserValidationAlgebra[F], cryptService: CryptService[F]) {

  def login(login:LoginRequest): EitherT[F, UserAuthenticationFailedError, User] = {
    val name = login.userName

    for {
      user <- getUserByName(name).leftMap(_ => UserAuthenticationFailedError(name))
      checkResult <- EitherT.liftF(cryptService.checkpw(login.password, cryptService.lift(user.hash)))
      resp <-
          if(checkResult == Verified) EitherT.rightT[F, UserAuthenticationFailedError](user)
          else EitherT.leftT[F, User](UserAuthenticationFailedError(name))
    } yield resp
  }

  def createUser(user: User): EitherT[F, UserAlreadyExistsError, User] =
    for {
      _ <- validation.doesNotExist(user)
      saved <- EitherT.liftF(userRepo.create(user))
    } yield saved

  def getUser(userId: Long): EitherT[F, UserNotFoundError.type, User] =
    EitherT.fromOptionF(userRepo.get(userId), UserNotFoundError)

  def getUserByName(userName: String): EitherT[F, UserNotFoundError.type, User] =
    EitherT.fromOptionF(userRepo.findByUserName(userName), UserNotFoundError)

  def deleteUser(userId: Long): F[Unit] = userRepo.delete(userId).as(())

  def deleteByUserName(userName: String): F[Unit] =
    userRepo.deleteByUserName(userName).as(())

  def update(user: User): EitherT[F, UserNotFoundError.type, User] =
    for {
      _ <- validation.exists(user.id)
      saved <- EitherT.fromOptionF(userRepo.update(user), UserNotFoundError)
    } yield saved

  def list(pageSize: Int, offset: Int): F[List[User]] =
    userRepo.list(pageSize, offset)
}

object UserService {
  def apply[F[_]: Monad: Sync](repository: UserRepositoryAlgebra[F], validation: UserValidationAlgebra[F], cryptService: CryptService[F]): UserService[F] =
    new UserService[F](repository, validation, cryptService)
}
