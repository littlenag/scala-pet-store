package io.github.pauljamescleary.petstore.client.pages

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import io.github.pauljamescleary.petstore.client._
import AppRouter.{AppPage, HomePageRt, RecoveryRt, RegisterRt}
import diode.data.PotState.PotEmpty
import typings.materialUiCore.components.{Button, Card, CardContent, CardHeader, TextField}
import io.github.pauljamescleary.petstore.client.services.SignIn
import io.github.pauljamescleary.petstore.client.services.UserProfile
import io.github.pauljamescleary.petstore.client.util._
import typings.materialUiCore.materialUiCoreStrings.{outlined, submit}
import typings.materialUiCore.mod.PropTypes.Margin

import scala.language.existentials

object SignInPage {

  case class Props(router: RouterCtl[AppPage], userProfile: ModelProxy[Pot[UserProfile]])

  case class State(username: String, password: String)

  import css.CssSettings._
  import scalacss.ScalaCssReact._

  object Style extends StyleSheet.Inline {
    import dsl._

    val outerDiv = style(
      textAlign.center,
      alignItems.flexStart,
      paddingTop(120.px),
      display.flex,
      flexDirection.column
    )

    val innerDiv = style(
      textAlign.left,
      minHeight(450.px),
      width(400.px),
      alignItems.flexStart,
      float.none,
      margin(0.px, auto)
    )

    val links = style(
      margin(17.px)
    )
  }

  class Backend($: ScalaComponent.BackendScope[Props, State]) {

    def signInForm(p: Props, s: State) = {
      <.div(
        <.form(^.onSubmit ==> { ev => p.userProfile.dispatchCB(SignIn(s.username, s.password)) },
          TextField.OutlinedTextFieldProps(
            variant = outlined,
            margin = Margin.normal,
            fullWidth = true,
            id = "email",
            label = "Email Address",
            name = "email",
            autoComplete = "email",
            autoFocus = true,
            required = true,
            onChange = _.withInputValue.map(text => $.modState(_.copy(username = text))).getOrElse(Callback.empty)
          )(),
          TextField.OutlinedTextFieldProps(
            variant = outlined,
            margin = Margin.normal,
            required = true,
            fullWidth = true,
            id = "password",
            label = "Password",
            name = "password",
            autoComplete = "current-password",
            `type` = "password",
            onChange = _.withInputValue.map(text => $.modState(_.copy(password = text))).getOrElse(Callback.empty)
          )(),
          Button(`type` = submit)("Submit")
        ),
        <.div(
          Style.links,
          <.span(p.router.link(RecoveryRt)("Forgot your password?")),
          <.br,
          <.span(p.router.link(RegisterRt)("Create an account."))
        )
      )
    }

    def render(p: Props, s: State) = {
      <.div(
        p.userProfile().renderReady { up =>
          // This seems to be the easy way to redirect. but have to make sure to run AFTER rendering!
          p.router.set(HomePageRt).async.unsafeToFuture()
          <.div(
            Style.outerDiv,
            up.toString
          )
        },
        p.userProfile().renderPending { _ =>
          <.div(
            Style.outerDiv,
            <.h3("Signing In...")
          )
        },
        p.userProfile().renderFailed { ex =>
          <.div(Style.outerDiv,
            <.div(Style.innerDiv,
              Card()(
                CardHeader()("Sign In -- Failed!"),
                CardContent()(signInForm(p,s))
              )
            )
          )
        },
        // Pending is conflated with Empty, so test state instead
        if (p.userProfile().state == PotEmpty) {
          <.div(Style.outerDiv,
            <.div(Style.innerDiv,
              Card()(
                CardHeader()("Sign In"),
                CardContent()(signInForm(p,s))
              )
            )
          )
        } else EmptyVdom
      )
    }
  }

  // create the React component
  val component = ScalaComponent.builder[Props]("Sign In")
      .initialState(State("", ""))
      .renderBackend[Backend]
      .build

  def apply(router: RouterCtl[AppPage], proxy: ModelProxy[Pot[UserProfile]]) = component(Props(router, proxy))
}
