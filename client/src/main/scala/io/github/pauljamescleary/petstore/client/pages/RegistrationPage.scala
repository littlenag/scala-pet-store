package io.github.pauljamescleary.petstore.client.pages

import diode.react.ReactPot._
import diode.data.Pot
import diode.data.PotState.PotEmpty
import diode.react._
import io.github.pauljamescleary.petstore.client.AppRouter.{AppPage, HomePageRt, SignInRt}
import io.github.pauljamescleary.petstore.client._
import io.github.pauljamescleary.petstore.client.services.{Register, UserProfile}
import io.github.pauljamescleary.petstore.client.util._
import typings.materialUiCore.components.{Button, Card, CardContent, CardHeader, TextField}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import typings.materialUiCore.materialUiCoreStrings.{outlined, submit}
import typings.materialUiCore.mod.PropTypes.Margin

import scala.language.existentials

object RegistrationPage {

  case class Props(router: RouterCtl[AppPage], userProfile: ModelProxy[Pot[UserProfile]])

  case class State(username: String, email:String, password1: String, password2: String)

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
      //fontSize(20.px),
      minHeight(450.px),
      width(400.px),
      alignItems.flexStart,
      float.none,
      margin(0 px, auto)
    )

    val links = style(
      margin(17 px)
    )
  }

  def signUpForm($: ScalaComponent.BackendScope[Props, State], p: Props, s: State) = {
    <.form(^.onSubmit ==> {ev => p.userProfile.dispatchCB(Register(s.username, s.email, s.password1))},
      TextField.OutlinedTextFieldProps(
        variant = outlined,
        margin = Margin.normal,
        required = true,
        fullWidth = true,
        id = "username",
        label = "Username",
        name = "username",
        autoComplete = "username",
        autoFocus = true,
        onChange = _.withInputValue.map(text => $.modState(_.copy(username = text))).getOrElse(Callback.empty)
      )(),
      TextField.OutlinedTextFieldProps(
        variant = outlined,
        margin = Margin.normal,
        required = true,
        fullWidth = true,
        id = "email",
        label = "Email",
        name = "email",
        autoComplete = "email",
        onChange = _.withInputValue.map(text => $.modState(_.copy(email = text))).getOrElse(Callback.empty)
      )(),
      TextField.OutlinedTextFieldProps(
        variant = outlined,
        margin = Margin.normal,
        required = true,
        fullWidth = true,
        id = "password",
        label = "Password",
        name = "password",
        //autoComplete = "current-password",
        `type` = "password",
        onChange = _.withInputValue.map(text => $.modState(_.copy(password1 = text))).getOrElse(Callback.empty)
      )(),
      TextField.OutlinedTextFieldProps(
        variant = outlined,
        margin = Margin.normal,
        required = true,
        fullWidth = true,
        id = "password2",
        label = "Confirm Password",
        name = "password2",
        `type` = "password",
        onChange = _.withInputValue.map(text => $.modState(_.copy(password2 = text))).getOrElse(Callback.empty)
      )(),
      Button(`type` = submit, disabled = {s.password1 != s.password2})("Submit"),
      <.div(
        Style.links,
        <.span("Already a member? ", p.router.link(SignInRt)("Sign in now"))
      )
    )
  }

  class Backend($: ScalaComponent.BackendScope[Props, State]) {
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
                CardHeader()(s"Sign Up -- There was an error: ${ex.getMessage}"),
                CardContent()(signUpForm($,p,s))
              )
            )
          )
        },
        // Pending is conflated with Empty, so test state instead
        if (p.userProfile().state == PotEmpty) {
          <.div(Style.outerDiv,
            <.div(Style.innerDiv,
              Card()(
                CardHeader()(s"Sign Up"),
                CardContent()(signUpForm($,p,s))
              )
            )
          )
        }
        else EmptyVdom
      )
    }
  }

    // create the React component for Dashboard
  private val component = ScalaComponent.builder[Props]("Sign Up")
    // create and store the connect proxy in state for later use
    .initialState(State("", "", "", ""))
    .renderBackend[Backend]
    .build

  def apply(router: RouterCtl[AppPage], proxy: ModelProxy[Pot[UserProfile]]) = component(Props(router, proxy))
}
