package io.github.pauljamescleary.petstore.frontend

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExportTopLevel
import dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._

import io.github.pauljamescleary.petstore._
import shared.domain.pets._
import components._
import services._
import pages._



/**
 * Tuturial WebApp entry point
 */
@JSExportTopLevel("PetstoreApp")
object PetstoreApp {

  // Define the pages used in the Petstore
  sealed trait Page
  case object HomePage extends Page
  case object SignInPage extends Page

  // base layout for all pages
  def layout(c: RouterCtl[Page], r: Resolution[Page]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "Petstore")),
          <.div(^.className := "collapse navbar-collapse")
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  // configure the router
  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._

    val petWrapper = AppCircuit.connect(_.pets)

    // wrap/connect components to the circuit
    (staticRoute(root, HomePage) ~> renderR(ctl => AppCircuit.wrap(_.motd)(proxy => Home(ctl, proxy)))

        ).notFound(redirectToPage(HomePage)(Redirect.Replace))
  }.renderWith(layout)

  def main(args: Array[String]): Unit = {
    println("Hello from the Petstore!")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    router().renderIntoDOM(dom.document.getElementById("root"))
  }
}