package io.github.pauljamescleary.petstore.client.pages

import diode.data.Pot
import diode.react._
import io.github.pauljamescleary.petstore.client.AppRouter.{AppPage, SignInRt}
import io.github.pauljamescleary.petstore.client._
import io.github.pauljamescleary.petstore.client.services.{SignOut, UserProfile}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

import scala.language.existentials

object SignOutPage {

  case class Props(router: RouterCtl[AppPage], userProfileProxy: ModelProxy[Pot[UserProfile]])

  import css.CssSettings._
  import scalacss.ScalaCssReact._

  object Style extends StyleSheet.Inline {
    import dsl._

    // Padding to push context below the navbar
    val innerDiv = style(
      paddingTop(40.px)
    )
  }

  // create the React component for Home page
  private val component = ScalaComponent.builder[Props]("Log Out Page")
      .renderP { (_, props) =>
        val cb = props.userProfileProxy.dispatchCB(SignOut) >> props.router.set(SignInRt)
        cb.async.runNow()
        <.div()
      }
      .build

  def apply(router: RouterCtl[AppPage], userProfile: ModelProxy[Pot[UserProfile]]) = component(Props(router, userProfile))
}
