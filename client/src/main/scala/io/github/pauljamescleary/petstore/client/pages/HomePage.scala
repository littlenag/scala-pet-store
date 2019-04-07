package io.github.pauljamescleary.petstore.client.pages

import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import io.github.pauljamescleary.petstore.client._
import AppRouter.{AppPage, SignInRt}
import io.github.littlenag.scalajs.components.reactbootstrap.{Button, Card, CardBody, CardTitle}
import io.github.pauljamescleary.petstore.client.components.Pets
import io.github.pauljamescleary.petstore.client.services.{ActivationEmail, AppCircuit, RootModel}

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
  .initialState(false)
  .renderPS { ($, props, disableResendButton) =>
    val userProfilePot = props.rootModel.zoom(_.userProfile).value

    // If the user hasn't authenticated re-direct to the sign-in page
    if (userProfilePot.isEmpty) {
      props.router.set(SignInRt).async.unsafeToFuture()
      <.div(Style.innerDiv)
    } else if (userProfilePot.map(! _.user.activated).getOrElse(false)) {
      // if not activated, then prompt to re-send activation email so they can activate
      <.div(Style.innerDiv,
        Card()(
          CardBody()(
            CardTitle()("Your account is not yet activated."),
            "You will need to activate your account before you can access the Pet Store.",
            <.br,
            // link to re-send activation email
            Button(disabled = disableResendButton)(
              ^.onClick --> {$.modState(_ => true) >> Callback(AppCircuit.dispatch(ActivationEmail(userProfilePot.map(_.user.email).getOrElse(""))))},
              "Re-send activation email."
            )
          )
        )
      )
    } else {
      <.div(Style.innerDiv,
        Pets(props.rootModel.zoom(_.pets))
      )
    }
  }
  .build

  def apply(router: RouterCtl[AppPage], rootModel: ModelProxy[RootModel]) = component(Props(router, rootModel))
}
