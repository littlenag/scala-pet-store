package io.github.pauljamescleary.petstore.frontend.services

import io.github.pauljamescleary.petstore._
import shared.domain.pets.Pet
import shared.PetstoreApi
import autowire._
import diode._
import diode.data._
import diode.util._
import diode.react.ReactConnector
import boopickle.Default._
import io.github.pauljamescleary.petstore.domain.authentication.LoginRequest
import io.github.pauljamescleary.petstore.shared.domain.users.User

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Actions :: Pet management
case object RefreshPets extends Action

case class UpdateAllPets(pets: Seq[Pet]) extends Action

case class UpdatePet(pet: Pet) extends Action

case class DeletePet(item: Pet) extends Action

case class UpdateMotd(potResult: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
  override def next(value: Pot[String]) = UpdateMotd(value)
}

// Actions :: Authentication
case class SignIn(username:String, password:String) extends Action

case class Authenticated(user:User) extends Action

// The base model of our application
case class RootModel(userProfile:Pot[UserProfile], pets: Pot[Pets], motd: Pot[String])

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
      // make a local update and inform server
      println("Tried to sign in")
      effectOnly(Effect(AjaxClient[PetstoreApi].signIn(LoginRequest(username,password)).call().flatMap {
        case None => Future.failed(new RuntimeException("Authentication Rejected"))
        case Some(user) => Future.successful(Authenticated(user))
      }))
    case Authenticated(user) =>
      updated(Ready(UserProfile(user)))
  }
}

case class Pets(items: Seq[Pet]) {
  def updated(newItem: Pet) = {
    items.indexWhere(_.id == newItem.id) match {
      case -1 =>
        // add new
        Pets(items :+ newItem)
      case idx =>
        // replace old
        Pets(items.updated(idx, newItem))
    }
  }
  def remove(item: Pet) = Pets(items.filterNot(_ == item))
}

/**
  * Handles actions related to pets
  *
  * @param modelRW Reader/Writer to access the model
  */
class PetHandler[M](modelRW: ModelRW[M, Pot[Pets]]) extends ActionHandler(modelRW) {
  override def handle: PartialFunction[Any, ActionResult[M]] = {
//    case RefreshPets =>
//      effectOnly(Effect(AjaxClient[PetstoreApi].getAllPets().call().map(UpdateAllPets)))
    case UpdateAllPets(pets) =>
      // got new todos, update model
      updated(Ready(Pets(pets)))
//    case UpdatePet(pet) =>
      // make a local update and inform server
//      updated(value.map(_.updated(pet)), Effect(AjaxClient[PetstoreApi].updateTodo(pet).call().map(UpdateAllTodos)))
//    case DeletePet(pet) =>
      // make a local update and inform server
//      updated(value.map(_.remove(pet)), Effect(AjaxClient[PetstoreApi].deleteTodo(pet.id).call().map(UpdateAllTodos)))
  }
}

/**
  * Handles actions related to the Motd
  *
  * @param modelRW Reader/Writer to access the model
  */
class MotdHandler[M](modelRW: ModelRW[M, Pot[String]]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action: UpdateMotd =>
      val updateF = action.effect(AjaxClient[PetstoreApi].welcomeMsg("User X").call())(identity)
      action.handleWith(this, updateF)(PotAction.handler())
  }
}

// Application circuit
object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(Empty, Unavailable, Unavailable)

  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new UserProfileHandler(zoomRW(_.userProfile)((m, v) => m.copy(userProfile = v))),
    new PetHandler(zoomRW(_.pets)((m, v) => m.copy(pets = v))),
    new MotdHandler(zoomRW(_.motd)((m, v) => m.copy(motd = v)))
  )
}