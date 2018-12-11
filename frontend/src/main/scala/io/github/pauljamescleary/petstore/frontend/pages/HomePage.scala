package io.github.pauljamescleary.petstore.frontend.pages

import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import io.github.pauljamescleary.petstore.frontend._
import AppRouter.{HomePageRt, AppPage}
import components._

import scala.language.existentials

object HomePage {

  case class Props(router: RouterCtl[AppPage], proxy: ModelProxy[Pot[String]])

  case class State(motdWrapper: ReactConnectProxy[Pot[String]])

  import css.CssSettings._
  import scalacss.ScalaCssReact._

  object Style extends StyleSheet.Inline {
    import dsl._

    val content = style(textAlign.center,
      fontSize(30.px),
      minHeight(450.px),
      paddingTop(40.px))
  }

  // create the React component for Dashboard
  private val component = ScalaComponent.builder[Props]("Home Page")
      // create and store the connect proxy in state for later use
      .initialStateFromProps(props => State(props.proxy.connect(m => m)))
      .renderPS { (_, props, state) =>
        <.div(
          Style.content,
          // header, MessageOfTheDay and chart components
          <.h2("Daily message"),
          state.motdWrapper(Motd(_)),
          //Chart(cp),
          // create a link to the HomePage
          <.div(props.router.link(HomePageRt)("Home"))
        )
      }
      .build

  def apply(router: RouterCtl[AppPage], proxy: ModelProxy[Pot[String]]) = component(Props(router, proxy))
}
