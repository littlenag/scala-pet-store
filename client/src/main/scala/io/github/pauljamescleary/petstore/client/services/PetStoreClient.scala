package io.github.pauljamescleary.petstore.client.services

import io.github.pauljamescleary.petstore.domain.authentication._
import io.github.pauljamescleary.petstore.domain.pets.Pet
import io.github.pauljamescleary.petstore.domain.users.User
import org.scalajs.dom
import typedapi.client._
import typedapi.client.js._
import org.scalajs.dom.ext.Ajax
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.github.pauljamescleary.petstore.domain.{PetAlreadyExistsError, PetNotFoundError, ValidationError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import io.github.pauljamescleary.petstore.shared.PetstoreApi

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object PetStoreClient {

  final case class DecodeException(msg: String) extends Exception

  implicit def decoder[A: io.circe.Decoder] = typedapi.util.Decoder[Future, A](json => decode[A](json).fold(
    error => Future.successful(Left(DecodeException(error.toString()))),
    user  => Future.successful(Right(user))
  ))
  implicit def encoder[A: io.circe.Encoder] = typedapi.util.Encoder[Future, A](obj => Future.successful(obj.asJson.noSpaces))

  // https://github.com/scala-js/scala-js-dom/issues/201
  private val getOrigin = {
    if (dom.window.location.origin.isDefined) {
      dom.window.location.origin.get
    } else {
      val port = if (dom.window.location.port.nonEmpty) ":" + dom.window.location.port else ""
      dom.window.location.protocol + "//" + dom.window.location.hostname + port
    }
  }

  type JwtToken = String

  private val cm = ClientManager(Ajax, getOrigin)

  private val (signInEP, signOutEP, registerEP, activationEmailEP, activateEP, recoveryEmailEP, validateResetTokenEP, resetPasswordEP) = deriveAll(PetstoreApi.AuthApi)

  def signIn(req: SignInRequest): Future[User] = signInEP(req).run[Future](cm)
  def signOut(authToken: AuthToken): Future[Unit] = signOutEP(authToken.value).run[Future](cm)

  def registerAccount(req: RegistrationRequest): Future[User] = registerEP(req).run[Future](cm)
  def activationEmail(): Future[Unit] = activationEmailEP(()).run[Future](cm)
  def activateAccount(token: String): Future[User] = activateEP(token).run[Future](cm)

  def recoveryEmail(): Future[Unit] = recoveryEmailEP(()).run[Future](cm)
  def validateResetToken(token:String): Future[Unit] = validateResetTokenEP(token).run[Future](cm)
  def resetPassword(req: PasswordResetRequest): Future[User] = resetPasswordEP(req.token, req).run[Future](cm)

  private val (listPetsEP, createPetEP, updatePetEP, deletePetEP) = deriveAll(PetstoreApi.PetsApi)

  def listPets(pageSize:Int = 10, offset:Int = 0): Future[List[Pet]] = listPetsEP(pageSize, offset).run[Future](cm)
  def createPet(req: Pet): Future[Either[PetAlreadyExistsError,Pet]] = {
    if (req.id.isDefined) Future.failed(new RuntimeException("Creation forbids an id."))
    else createPetEP(req).run[Future](cm)
  }
  def updatePet(req: Pet): Future[Either[PetNotFoundError.type,Pet]] = {
    req.id match {
      case None => Future.failed(new RuntimeException("Updating requires an id."))
      case Some(id) => updatePetEP(id,req).run[Future](cm)
    }
  }
  def deletePet(id: Long): Future[Unit] = deletePetEP(id).run[Future](cm)

  def upsertPet(req:Pet): Future[Either[ValidationError, Pet]] = {
    if (req.id.isDefined) updatePet(req)
    else createPet(req)
  }
}