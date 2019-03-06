package io.github.pauljamescleary.petstore.client.pages

import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import io.github.pauljamescleary.petstore.client._
import AppRouter.{AppPage, SignInRt}
import io.github.pauljamescleary.petstore.client.components.Pets
import io.github.pauljamescleary.petstore.client.services.RootModel

import scala.language.existentials

object HomePage {

  case class Props(router: RouterCtl[AppPage], rootModel: ModelProxy[RootModel])

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
  private val component = ScalaComponent.builder[Props]("Home Page")
      .renderP { (_, props) =>
        // If the user hasn't authenticated re-direct to the sign-in page
        if (props.rootModel.zoom(_.userProfile).value.isEmpty) {
          props.router.set(SignInRt).async.runNow()
          <.div()
        } else {
          <.div(Style.innerDiv, Pets(props.rootModel.zoom(_.pets)))
        }
      }
      .build

  def apply(router: RouterCtl[AppPage], rootModel: ModelProxy[RootModel]) = component(Props(router, rootModel))
}
