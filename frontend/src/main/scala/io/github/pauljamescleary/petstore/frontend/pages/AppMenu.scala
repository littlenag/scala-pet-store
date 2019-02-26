package io.github.pauljamescleary.petstore.frontend.pages

import io.github.pauljamescleary.petstore.frontend._
import AppRouter._
import models.Menu
import css.CssSettings._
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import diode.react.ModelProxy
import io.github.pauljamescleary.petstore.frontend.services.UserProfile
import scalacss.ScalaCssReact._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Reusability
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

/**
  * Navigation menu for the application.
  */
object AppMenu {

  import io.github.pauljamescleary.petstore.frontend.css.GlobalStyles._

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
    Menu("Sign Up", SignUpRt)
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
      <.ul.apply(
        ^.`class` := "nav navbar-nav navbar-right",
        unauthenticatedMenu.toTagMod { item =>
          <.li(
            ^.key := item.name,
            Style.menuItem(item.route.getClass == p.selectedPage.getClass),
            <.a(^.`class` := "navbar-brand", p.ctrl setOnClick item.route)(item.name)
          )
        }
      )
    }

    def authenticated(userProfile: UserProfile, p: Props) = {
      <.ul.apply(
        ^.`class` := "nav navbar-nav navbar",
        authenticatedMenu.toTagMod { item =>
          <.li(
            ^.key := item.name,
            Style.menuItem(item.route.getClass == p.selectedPage.getClass),
            <.a(^.`class` := "navbar-brand", p.ctrl setOnClick item.route)(item.name)
          )
        }
      )
    }

    def render(p: Props, s: State) = {
      <.header(
        <.nav(bootstrapStyles.navbar,
          <.div(^.`class` := "container-fluid",
            <.div(^.`class` := "navbar-header",
              <.a(^.`class` := "navbar-brand", p.ctrl setOnClick HomePageRt)("Pet Store")
            ),
            p.userProfile().render { up => authenticated(up,p)},
            p.userProfile().renderEmpty { unauthenticated(p)},
          )
        )
      )
    }
  }

  // create the React component
  val component = ScalaComponent.builder[Props]("AppMenu")
    .initialState(State())
    .renderBackend[Backend]
    //.configure(Reusability.shouldComponentUpdate)
    .build

  def apply(proxy: ModelProxy[Pot[UserProfile]],
            selectedPage: AppPage,
            ctrl: RouterCtl[AppPage]) = component(Props(proxy,selectedPage,ctrl))

}
