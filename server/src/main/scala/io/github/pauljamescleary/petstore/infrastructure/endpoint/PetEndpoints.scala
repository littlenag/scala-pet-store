package io.github.pauljamescleary.petstore.infrastructure.endpoint

import cats.data.Validated.Valid
import cats.data._
import cats.effect.Effect
import cats.implicits._
import io.github.pauljamescleary.petstore.domain.crypt.AuthService
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes, QueryParamDecoder}
import tsec.authentication._

import scala.language.higherKinds
import io.github.pauljamescleary.petstore.domain.{PetAlreadyExistsError, PetNotFoundError}
import io.github.pauljamescleary.petstore.domain.pets.{Pet, PetService, PetStatus}

class PetEndpoints[F[_]: Effect](petService: PetService[F], authService: AuthService[F]) extends Http4sDsl[F] {

  import Pagination._

  /* Parses out status query param which could be multi param */
  implicit val statusQueryParamDecoder: QueryParamDecoder[PetStatus] =
    QueryParamDecoder[String].map(PetStatus.withName)

  /* Relies on the statusQueryParamDecoder implicit, will parse out a possible multi-value query parameter */
  object StatusMatcher extends OptionalMultiQueryParamDecoderMatcher[PetStatus]("status")

  /* Parses out tag query param, which could be multi-value */
  object TagMatcher extends OptionalMultiQueryParamDecoderMatcher[String]("tags")

  implicit val petDecoder: EntityDecoder[F, Pet] = jsonOf[F, Pet]

  private val createPetEndpoint: HttpRoutes[F] =
    authService.liftRoute {
      case req @ POST -> Root / "pets" asAuthed _ =>
        val action = for {
          pet <- req.request.as[Pet]
          result <- petService.create(pet).value
        } yield result

        action.flatMap {
          case Right(saved) =>
            Ok(saved.asJson)
          case Left(PetAlreadyExistsError(existing)) =>
            Conflict(s"The pet ${existing.name} of category ${existing.category} already exists")
        }
    }

  private val updatePetEndpoint: HttpRoutes[F] =
    authService.liftRoute {
      case req @ PUT -> Root / "pets" / LongVar(petId) asAuthed user =>
        val action = for {
          pet <- req.request.as[Pet]
          updated = pet.copy(id = Some(petId))
          result <- petService.update(pet).value
        } yield result

        action.flatMap {
          case Right(saved) => Ok(saved.asJson)
          case Left(PetNotFoundError) => NotFound("The pet was not found")
        }
    }

  private val getPetEndpoint: HttpRoutes[F] =
    authService.liftRoute {
      case GET -> Root / "pets" / LongVar(id) asAuthed user =>
        petService.get(id).value.flatMap {
          case Right(found) => Ok(found.asJson)
          case Left(PetNotFoundError) => NotFound("The pet was not found")
        }
    }

  private val deletePetEndpoint: HttpRoutes[F] =
    authService.liftRoute {
      case DELETE -> Root / "pets" / LongVar(id) asAuthed user =>
        for {
          _ <- petService.delete(id)
          resp <- Ok()
        } yield resp
    }

  private val listPetsEndpoint: HttpRoutes[F] =
    authService.liftRoute {
      case GET -> Root / "pets" :? OptionalPageSizeMatcher(pageSize) :? OptionalOffsetMatcher(offset) asAuthed user =>
        for {
          retrieved <- petService.list(pageSize.getOrElse(10), offset.getOrElse(0))
          resp <- Ok(retrieved.asJson)
        } yield resp
    }

  private val findPetsByStatusEndpoint: HttpRoutes[F] =
    authService.liftRoute {
      case GET -> Root / "pets" / "findByStatus" :? StatusMatcher(Valid(Nil)) asAuthed user =>
        // User did not specify any statuses
        BadRequest("status parameter not specified")

      case GET -> Root / "pets" / "findByStatus" :? StatusMatcher(Valid(statuses)) asAuthed user =>
        // We have a list of valid statuses, find them and return
        for {
          retrieved <- petService.findByStatus(NonEmptyList.fromListUnsafe(statuses))
          resp <- Ok(retrieved.asJson)
        } yield resp
    }

  private val findPetsByTagEndpoint: HttpRoutes[F] =
    authService.liftRoute {
      case GET -> Root / "pets" / "findByTags" :? TagMatcher(Valid(Nil)) asAuthed user =>
        BadRequest("tag parameter not specified")

      case GET -> Root / "pets" / "findByTags" :? TagMatcher(Valid(tags)) asAuthed user =>
        for {
          retrieved <- petService.findByTag(NonEmptyList.fromListUnsafe(tags))
          resp <- Ok(retrieved.asJson)
        } yield resp
    }

  def endpoints: HttpRoutes[F] =
    createPetEndpoint <+>
      getPetEndpoint <+>
      deletePetEndpoint <+>
      listPetsEndpoint <+>
      findPetsByStatusEndpoint <+>
      updatePetEndpoint <+>
      findPetsByTagEndpoint
}

object PetEndpoints {
  def endpoints[F[_]: Effect](petService: PetService[F], authService: AuthService[F]): HttpRoutes[F] =
    new PetEndpoints[F](petService,authService).endpoints
}
