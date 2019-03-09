package io.github.pauljamescleary.petstore

import cats.data.{Kleisli, OptionT}
import config._
import domain.users._
import domain.orders._
import domain.pets._
import infrastructure.endpoint._
import infrastructure.repository.doobie.{DoobieOrderRepositoryInterpreter, DoobiePetRepositoryInterpreter, DoobieUserRepositoryInterpreter}
import cats.effect._
import cats.implicits._
import org.http4s.server.{AuthMiddleware, Router, Server => H4Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import io.circe.config.parser
import io.github.pauljamescleary.petstore.domain.crypt.AuthService
import org.http4s.Request

object Server extends IOApp {

  implicit def appContextShift: ContextShift[IO] = super.contextShift



  def createServer[F[_] : ContextShift : ConcurrentEffect : Timer]: Resource[F, H4Server[F]] = {
    for {
      conf           <- Resource.liftF(parser.decodePathF[F, PetStoreConfig]("petstore"))
      xa             <- DatabaseConfig.dbTransactor(conf.db, global, global)
      petRepo        =  DoobiePetRepositoryInterpreter[F](xa)
      orderRepo      =  DoobieOrderRepositoryInterpreter[F](xa)
      userRepo       =  DoobieUserRepositoryInterpreter[F](xa)
      petValidation  =  PetValidationInterpreter[F](petRepo)
      authService    =  AuthService.bcrypt[F](userRepo)
      petService     =  PetService[F](petRepo, petValidation)
      userValidation =  UserValidationInterpreter[F](userRepo)
      orderService   =  OrderService[F](orderRepo)
      userService    =  UserService[F](userRepo,userValidation,authService)
      services       =  PetEndpoints.endpoints[F](petService,authService) <+>
                            OrderEndpoints.endpoints[F](orderService) <+>
                            UserEndpoints.endpoints[F](userService) <+>
                            AuthEndpoints.endpoints[F](userService,authService) <+>
                            FrontendEndpoints.endpoints[F]()
      httpApp = Router("/" -> services).orNotFound
      _ <- Resource.liftF(DatabaseConfig.initializeDb(conf.db))
      // Add some test data
      _ <- Resource.liftF(petService.create(Pet("Fred", "dog", "very friendly")).value)
      _ <- Resource.liftF(petService.create(Pet("Emmy", "cat", "meow")).value)
      _ <- Resource.liftF(petService.create(Pet("Carrie", "dog", "sleepy")).value)
      server <-
        BlazeServerBuilder[F]
        .bindHttp(conf.server.port, conf.server.host)
        .withHttpApp(httpApp)
        .resource
    } yield server
  }

  def run(args : List[String]) : IO[ExitCode] = createServer.use(_ => IO.never).as(ExitCode.Success)
}
