package io.github.pauljamescleary.petstore.frontend.pages

import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import io.github.pauljamescleary.petstore.frontend._
import AppRouter.AppPage
import io.github.pauljamescleary.petstore.frontend.services.RootModel

import scala.language.existentials

object HomePage {

  case class Props(router: RouterCtl[AppPage], rootModel: ModelProxy[RootModel])

  //case class State()

  import css.CssSettings._
  import scalacss.ScalaCssReact._

  object Style extends StyleSheet.Inline {
    import dsl._

    val content = style(textAlign.center,
      //fontSize(30.px),
      minHeight(450.px),
      paddingTop(40.px))
  }

  // create the React component for Dashboard
  private val component = ScalaComponent.builder[Props]("Home Page")
      // create and store the connect proxy in state for later use
      //.initialStateFromProps(props => State(props.proxy))
      .renderP { (_, props) =>
        <.div(Style.content,
          // header, MessageOfTheDay and chart components
          <.h3("Home page")
          //state.motdWrapper(Motd(_)),
          //Chart(cp),
          // create a link to the HomePage
          //<.div(props.router.link(HomePageRt)("Home"))
        )
      }
      .build

  def apply(router: RouterCtl[AppPage], rootModel: ModelProxy[RootModel]) = component(Props(router, rootModel))
}
