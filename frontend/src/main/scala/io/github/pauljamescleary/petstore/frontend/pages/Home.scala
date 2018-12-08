package io.github.pauljamescleary.petstore.frontend.pages

import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import io.github.pauljamescleary.petstore.frontend._
import PetstoreApp.{Page, HomePage}
import components._

import scala.util.Random
import scala.language.existentials

object Home {

  case class Props(router: RouterCtl[Page], proxy: ModelProxy[Pot[String]])

  case class State(motdWrapper: ReactConnectProxy[Pot[String]])

  // create the React component for Dashboard
  private val component = ScalaComponent.builder[Props]("Home Page")
      // create and store the connect proxy in state for later use
      .initialStateFromProps(props => State(props.proxy.connect(m => m)))
      .renderPS { (_, props, state) =>
        <.div(
          // header, MessageOfTheDay and chart components
          <.h2("Daily message"),
          state.motdWrapper(Motd(_)),
          //Chart(cp),
          // create a link to the To Do view
          <.div(props.router.link(HomePage)("Check your todos!"))
        )
      }
      .build

  def apply(router: RouterCtl[Page], proxy: ModelProxy[Pot[String]]) = component(Props(router, proxy))
}
