package io.github.pauljamescleary.petstore.client.services

import java.util.UUID

import io.github.pauljamescleary.petstore._
import client.logger._
import domain.pets.Pet
import diode._
import diode.data._
import diode.react.ReactConnector
import io.github.pauljamescleary.petstore.domain.authentication._
import io.github.pauljamescleary.petstore.domain.users.User

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Actions :: Pet management
case object RefreshPets extends Action

case object PetsModified extends Action

case class UpdateAllPets(pets: Seq[Pet]) extends Action

case class UpsertPet(pet: Pet) extends Action
case class UpdateFailed(pet: Pet) extends Action

case class DeletePet(item: Pet) extends Action
case class DeleteFailed(item: Pet) extends Action

// Actions :: Authentication
case class SignIn(username:String, password:String) extends Action
case class Authenticated(user:User) extends Action
case class SignInError(ex:Throwable) extends Action

case object SignOut extends Action

case class Register(username:String, email:String, password:String) extends Action
case class AccountCreated(user:User) extends Action
case class RegistrationError(ex:Throwable) extends Action

case class PasswordRecovery(email:String) extends Action
case class PasswordReset(token:String, newPassword:String) extends Action

case class ActivationEmail(email:String) extends Action

// The base model of our application
case class RootModel(userProfile:Pot[UserProfile], pets: Pot[PetsData])

case class UserProfile(user:User) {
  def updated(user: User): UserProfile = {
    UserProfile(user)
  }
}

/**
  * Handles actions related to authentication
  *
  * @param modelRW Reader/Writer to access the model
  */
class UserProfileHandler[M](modelRW: ModelRW[M, Pot[UserProfile]]) extends ActionHandler(modelRW) {
  override def handle = {
    case SignIn(username, password) =>
      log.debug("Tried to sign in")
      updated(Pending(),
        Effect(
          PetStoreClient.signIn(SignInRequest(username,password))
              .map[Action] { resp => Authenticated(resp.user) }
              .recover { case x => SignInError(x) }
        )
      )
    case Authenticated(user) =>
      log.debug("Sign in accepted")
      updated(Ready(UserProfile(user)))

    case SignInError(ex) =>
      log.debug("Sign in failed")
      updated(Failed(ex))

    case SignOut =>
      log.debug("Signing out")
      updated(Empty)

    case Register(username, email, password) =>
      log.debug("Tried to register account")
      updated(Pending(),
        Effect(
          PetStoreClient.registerAccount(RegistrationRequest(username,"", "", email, password, ""))
              .map[Action] { user => AccountCreated(user) }
              .recover { case x => RegistrationError(x) }
        )
      )
    case AccountCreated(user) =>
      log.debug("Registration accepted")
      updated(Ready(UserProfile(user)))

    case RegistrationError(ex) =>
      log.debug("Registration failed")
      updated(Failed(ex))

    case PasswordReset(token, newPassword) =>
      log.debug("Password reset")
      effectOnly(
        Effect(
          PetStoreClient.resetPassword(token, PasswordResetRequest(newPassword))
            .map{ _ => NoAction }
        )
      )

    case PasswordRecovery(email) =>
      log.debug("Password recovery email")
      effectOnly(
        Effect(
          PetStoreClient.recoveryEmail(PasswordRecoveryRequest(email))
            .map{ _ => NoAction }
        )
      )

    case ActivationEmail(email) =>
      log.debug("Account activation email")
      effectOnly(
        Effect(
          PetStoreClient.activationEmail(ActivationEmailRequest(email))
          .map{ _ => NoAction }
        )
      )
  }
}

case class PetsData(pets: Seq[Pet]) {
  def updated(newPet: Pet) = {
    pets.indexWhere(_.id == newPet.id) match {
      case -1 =>
        // add new
        PetsData(pets :+ newPet)
      case idx =>
        // replace old
        PetsData(pets.updated(idx, newPet))
    }
  }
  def remove(pet: Pet) = PetsData(pets.filterNot(_ == pet))
}

/**
  * Handles actions related to pets
  *
  * @param modelRW Reader/Writer to access the model
  */
class PetHandler[M](modelRW: ModelRW[M, Pot[PetsData]]) extends ActionHandler(modelRW) {
  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshPets =>
      effectOnly(Effect(PetStoreClient.listPets().map(UpdateAllPets)))
    case UpdateAllPets(pets) =>
      // got new pets, update model
      updated(Ready(PetsData(pets)))
    case UpsertPet(pet) =>
      // make a local update and inform server
      val eff = Effect(PetStoreClient.upsertPet(pet).map(_ => NoAction).recover{case _ => RefreshPets})
      updated(value.map(_.updated(pet)), eff)
    case DeletePet(pet) =>
      // make a local update and inform server
      val eff = pet.id match {
        case Some(id) => Effect(PetStoreClient.deletePet(id).map(_ => NoAction).recover{case _ => RefreshPets})
        case None => Effect.action(NoAction)
      }
      updated(value.map(_.remove(pet)), eff)
  }
}

// Application circuit
object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {

  // initial application model
  override protected def initialModel = RootModel(
    Empty,         // because the user isn't logged in
    Unavailable    // would become available once the user logs in
  )

  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new UserProfileHandler(zoomRW(_.userProfile)((m, v) => m.copy(userProfile = v))),
    new PetHandler(zoomRW(_.pets)((m, v) => m.copy(pets = v)))
  )
}