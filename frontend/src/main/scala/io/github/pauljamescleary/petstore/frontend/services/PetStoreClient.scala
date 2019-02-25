package io.github.pauljamescleary.petstore.frontend.services

import io.github.pauljamescleary.petstore.domain.authentication.{LoginRequest, SignupRequest}
import io.github.pauljamescleary.petstore.domain.pets.Pet
import io.github.pauljamescleary.petstore.domain.users.User
import org.scalajs.dom

import typedapi.client._
//import typedapi.client.js._
import org.scalajs.dom.ext.Ajax

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
//import io.github.pauljamescleary.petstore.shared.JsonSerializers._
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
  val getOrigin = {
    if (dom.window.location.origin.isDefined) {
      dom.window.location.origin.get
    } else {
      val port = if (dom.window.location.port.nonEmpty) ":" + dom.window.location.port else ""
      dom.window.location.protocol + "//" + dom.window.location.hostname + port
    }
  }

  val cm = ClientManager(Ajax, getOrigin)

  val (loginT, signupT) = deriveAll(PetstoreApi.Api)

  def login(req: LoginRequest): Future[User] = {
    //loginT(req).run[Future](cm)
    Future.failed(new RuntimeException(""))
  }

  def signup(req: SignupRequest): Future[User] = {
    //signupT(req).run[Future](cm)
    Future.failed(new RuntimeException(""))
  }
}