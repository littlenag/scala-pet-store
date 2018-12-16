package io.github.pauljamescleary.petstore.frontend.services

import io.github.pauljamescleary.petstore.domain.authentication.{LoginRequest, SignupRequest}
import io.github.pauljamescleary.petstore.shared.domain.users.User
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import io.github.pauljamescleary.petstore.shared.JsonSerializers._

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object UsersClient {

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

}
