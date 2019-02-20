package io.github.pauljamescleary.petstore.frontend.services

import io.github.pauljamescleary.petstore.domain.authentication.{LoginRequest, SignupRequest}
import io.github.pauljamescleary.petstore.domain.users.User
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import io.github.pauljamescleary.petstore.shared.JsonSerializers._
import io.github.pauljamescleary.petstore.shared.domain.pets.Pet

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object PetStoreClient {

  def login(req: LoginRequest): Future[User] = {
    dom.ext.Ajax.post(
      url = "/login",
      data = req.asJson.spaces2,
      headers = Map("Content-Type" -> "application/json")
    ).flatMap { resp =>
      Future.fromTry(decodeJson[User](resp.responseText).toTry)
    }
  }

  def signup(req: SignupRequest): Future[User] = {
    dom.ext.Ajax.post(
      url = "/users",
      data = req.asJson.spaces2,
      headers = Map("Content-Type" -> "application/json")
    ).flatMap { resp =>
      Future.fromTry(decodeJson[User](resp.responseText).toTry)
    }
  }

  //

  def createPet(req: Pet): Future[Pet] = {
    dom.ext.Ajax.post(
      url = "/pets",
      data = req.asJson.spaces2,
      headers = Map("Content-Type" -> "application/json")
    ).flatMap { resp =>
      Future.fromTry(decodeJson[Pet](resp.responseText).toTry)
    }
  }

}
