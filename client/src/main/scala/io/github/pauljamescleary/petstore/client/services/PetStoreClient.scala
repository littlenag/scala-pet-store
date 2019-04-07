package io.github.pauljamescleary.petstore.client.services

import io.github.pauljamescleary.petstore.domain.authentication.{ActivationEmailRequest, _}
import io.github.pauljamescleary.petstore.domain.pets.Pet
import io.github.pauljamescleary.petstore.domain.users.User
import io.github.pauljamescleary.petstore.client.logger._
import org.scalajs.dom
import typedapi.client._
import typedapi.client.js._
import org.scalajs.dom.ext.Ajax
//import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.github.pauljamescleary.petstore.domain.{PetAlreadyExistsError, PetNotFoundError, ValidationError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import io.github.pauljamescleary.petstore.shared.PetstoreApi
//import typedapi.util

object PetStoreClient {

  final case class DecodeException(msg: String) extends Exception

  implicit def typDecoder[A: io.circe.Decoder] = typedapi.util.Decoder[Future, A](json =>
    decode[A](json).fold(
      error => Future.successful(Left(DecodeException(error.toString))),
      user  => Future.successful(Right(user))
    )
  )
  implicit def typEncoder[A: io.circe.Encoder] = typedapi.util.Encoder[Future, A](obj => Future.successful(obj.asJson.noSpaces))

  implicit val decodeUnit = typedapi.util.Decoder[Future, Unit]{_ => Future.successful(Right(()))}
  implicit val encodeUnit = typedapi.util.Encoder[Future, Unit](_ => Future.successful(""))

  // https://github.com/scala-js/scala-js-dom/issues/201
  private val getOrigin = {
    if (dom.window.location.origin.isDefined) {
      dom.window.location.origin.get
    } else {
      val port = if (dom.window.location.port.nonEmpty) ":" + dom.window.location.port else ""
      dom.window.location.protocol + "//" + dom.window.location.hostname + port
    }
  }

  private var bearerToken: Option[AuthToken] = None

  // If you don't want to use local storage because it's too insecure, then store in memory.
  def setBearerToken(authToken: AuthToken) = bearerToken = Some(authToken)
  def getBearerToken: Option[AuthToken] = bearerToken

  val AUTH_TOKEN_KEY = "scala-pet-store-auth-token"

  // If you are comfortable with local storage, then we use these.
  def storeBearerToken(authToken: AuthToken) = dom.window.localStorage.setItem(AUTH_TOKEN_KEY, authToken.value)
  def retrieveBearerToken: Option[AuthToken] = Option(dom.window.localStorage.getItem(AUTH_TOKEN_KEY)).map(AuthToken(_))

  def authTokenHeader = {
    retrieveBearerToken.map(token => "Bearer " + token.value).getOrElse("")
  }

  private val cm = ClientManager(Ajax, getOrigin)

  private val (signInEP, signOutEP, registerEP, activationEmailEP, activateEP, recoveryEmailEP, validateResetTokenEP, resetPasswordEP) = deriveAll(PetstoreApi.AuthApi)

  def signIn(req: SignInRequest): Future[SignInResponse] = {
    signInEP(req).run[Future](cm).map { resp: SignInResponse =>
      log.debug(s"Auth token: ${resp.auth}")
      storeBearerToken(resp.auth)
      resp
    }
  }
  def signOut(authToken: AuthToken): Future[Unit] = {
    signOutEP(authToken.value).run[Future](cm)
  }

  def registerAccount(req: RegistrationRequest): Future[User] = registerEP(req).run[Future](cm)
  def activationEmail(req: ActivationEmailRequest): Future[Unit] = activationEmailEP(req).run[Future](cm)
  def activateAccount(token: String): Future[Unit] = activateEP(token).run[Future](cm)

  def recoveryEmail(req: PasswordRecoveryRequest): Future[Unit] = recoveryEmailEP(req).run[Future](cm)
  def validateResetToken(token:String): Future[Unit] = validateResetTokenEP(token.toString).run[Future](cm)
  def resetPassword(token:String, req: PasswordResetRequest): Future[User] = resetPasswordEP(token, req).run[Future](cm)

  private val (listPetsEP, createPetEP, updatePetEP, deletePetEP) = deriveAll(PetstoreApi.PetsApi)

  def listPets(pageSize:Int = 10, offset:Int = 0): Future[List[Pet]] = listPetsEP(pageSize, offset, authTokenHeader).run[Future](cm)
  def createPet(req: Pet): Future[Either[PetAlreadyExistsError,Pet]] = {
    if (req.id.isDefined) Future.failed(new RuntimeException("Creation forbids an id."))
    else createPetEP(authTokenHeader, req).run[Future](cm)
  }
  def updatePet(req: Pet): Future[Either[PetNotFoundError.type,Pet]] = {
    req.id match {
      case None => Future.failed(new RuntimeException("Updating requires an id."))
      case Some(id) => updatePetEP(id, authTokenHeader, req).run[Future](cm)
    }
  }
  def deletePet(id: Long): Future[Unit] = deletePetEP(id, authTokenHeader).run[Future](cm)

  def upsertPet(req:Pet): Future[Either[ValidationError, Pet]] = {
    if (req.id.isDefined) updatePet(req)
    else createPet(req)
  }
}