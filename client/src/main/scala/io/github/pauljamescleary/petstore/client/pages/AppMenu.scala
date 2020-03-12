package io.github.pauljamescleary.petstore.client.pages

import io.github.pauljamescleary.petstore.client._
import AppRouter._
import models.Menu
import css.CssSettings._
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import diode.react.ModelProxy
import typings.materialUiCore.components.{AppBar, Button, Card, CardContent, TextField, Toolbar}
import typings.materialUiCore.materialUiCoreStrings.static

import scala.scalajs.js
//import io.github.pauljamescleary.petstore.client.bootstrap.{Nav, NavLink, Navbar, NavbarBrand}
import io.github.pauljamescleary.petstore.client.services.UserProfile
import scalacss.ScalaCssReact._
import japgolly.scalajs.react._
//import japgolly.scalajs.react.extra.Reusability
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

/**
  * Navigation menu for the application.
  *
  * Relies directly on bootstrap navbar being loaded.
  */
object AppMenu {

  object Style extends StyleSheet.Inline {

    import dsl._

    val navMenu = style(display.flex,
      alignItems.center,
      backgroundColor(c"#F2706D"),
      margin.`0`,
      listStyle := "none")

    val menuItem = styleF.bool { selected =>
      styleS(
        //padding(20.px),
        fontSize(1.5.em),
        cursor.pointer,
        color(c"rgb(244, 233, 233)")
      )
    }
  }

  val unauthenticatedMenu = Vector(
    Menu("Sign In", SignInRt),
    Menu("Sign Up", RegisterRt)
  )

  val authenticatedMenu = Vector(
    Menu("Home", HomePageRt)
  )

  case class Props(userProfile: ModelProxy[Pot[UserProfile]],
                   selectedPage: AppPage,
                   ctrl: RouterCtl[AppPage])

  case class State()

  implicit val currentPageReuse = Reusability.by_==[AppPage]
  implicit val propsReuse = Reusability.by((_: Props).selectedPage)

  class Backend($: BackendScope[Props, State]) {
    def unauthenticated(p: Props) = {
      <.div(
        Button(href = p.ctrl.pathFor(SignInRt).value)("Sign In"),
        Button(href = p.ctrl.pathFor(RegisterRt).value)("Sign Up"),
      )
    }

    def authenticated(userProfile: UserProfile, p: Props) = {
      Button(href = p.ctrl.pathFor(SignOutRt).value)("Sign Out")
    }

    def render(p: Props, s: State) = {
      <.header(
        AppBar(position = static)(
          Toolbar()(
            Button(href = p.ctrl.pathFor(HomePageRt).value)("Pet Store"),
            p.userProfile().render { up => authenticated(up,p)},
            p.userProfile().renderEmpty { unauthenticated(p) }
          )
        )
      )
    }
  }

  // create the React component
  val component = ScalaComponent.builder[Props]("AppMenu")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(proxy: ModelProxy[Pot[UserProfile]],
            selectedPage: AppPage,
            ctrl: RouterCtl[AppPage]) = component(Props(proxy,selectedPage,ctrl))

}
