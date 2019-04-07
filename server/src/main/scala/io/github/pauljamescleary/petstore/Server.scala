package io.github.pauljamescleary.petstore

import config._
import domain.users._
import domain.orders._
import domain.pets._
import infrastructure.endpoint._
import infrastructure.repository.doobie._
import cats.effect._
import cats.implicits._
import org.http4s.server.{Router, Server => H4Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import io.circe.config.parser
import io.github.pauljamescleary.petstore.domain.auth.AuthService
import io.github.pauljamescleary.petstore.domain.mailer.MailerService
import org.http4s.dsl.Http4sDsl

object Server extends IOApp {

  implicit def appContextShift: ContextShift[IO] = super.contextShift

  def createServer[F[_] : ContextShift : ConcurrentEffect : Timer]: Resource[F, H4Server[F]] = {
    for {
      conf           <- Resource.liftF(parser.decodePathF[F, PetStoreConfig]("petstore"))

      xa             <- DatabaseConfig.dbTransactor(conf.db, global, global)
      petRepo        =  DoobiePetRepositoryInterpreter[F](xa)
      orderRepo      =  DoobieOrderRepositoryInterpreter[F](xa)
      userRepo       =  DoobieUserRepositoryInterpreter[F](xa)
      authInfoRepo   =  DoobieAuthInfoRepositoryInterpreter[F](xa)

      userValidation =  UserValidationInterpreter[F](userRepo)
      petValidation  =  PetValidationInterpreter[F](petRepo)

      mailerService  =  MailerService[F](conf.mailer,conf.baseUrl)
      authService    =  AuthService[F](mailerService,userRepo,authInfoRepo)
      petService     =  PetService[F](petRepo, petValidation)
      orderService   =  OrderService[F](orderRepo)
      userService    =  UserService[F](userRepo,userValidation,authService)

      tp1 = new TestEndpoints1[F](authService)
      tp2 = new TestEndpoints2[F](authService)

      services       =
        PetEndpoints.endpoints[F](petService,authService) <+>
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
      hashpw <- Resource.liftF(authService.hashPassword("test"))
      _ <- Resource.liftF(userService.createUser(User("test", "test", "test", "test@test.com", hashpw.toString, "", "User", false)).value)
      server <-
        BlazeServerBuilder[F]
        .bindHttp(conf.server.port, conf.server.host)
        .withHttpApp(httpApp)
        .resource
    } yield server
  }

  def run(args : List[String]) : IO[ExitCode] = createServer.use(_ => IO.never).as(ExitCode.Success)
}

class TestEndpoints1[F[_]: Sync](authService: AuthService[F]) extends Http4sDsl[F] {

  import tsec.authentication._

  val ep: TSecAuthService[User, TSecBearerToken[Long], F] =
    //TSecAuthService.withAuthorization(authService.UserRequired) {
    TSecAuthService {
      case req @ GET -> Root / "foo" asAuthed user =>
        Ok(s"user ${user.userName} says hi foo")

      case req @ GET -> Root / "qaz" asAuthed user =>
        Ok(s"user ${user.userName} says hi qaz")
    }
}

class TestEndpoints2[F[_]: Sync](authService: AuthService[F]) extends Http4sDsl[F] {

  import tsec.authentication._

  val ep: TSecAuthService[User, TSecBearerToken[Long], F] =
    //TSecAuthService.withAuthorization(authService.UserRequired) {
    TSecAuthService {
      case req @ GET -> Root / "bar" asAuthed user =>
        Ok(s"user ${user.userName} says hi bar")
    }
}