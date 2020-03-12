package io.github.pauljamescleary.petstore.client.pages

import java.util.UUID

import io.github.pauljamescleary.petstore.client.AppRouter.{AppPage, RegisterRt, SignInRt}
import io.github.pauljamescleary.petstore.client._
import io.github.pauljamescleary.petstore.client.util._
import io.github.pauljamescleary.petstore.client.services.{AppCircuit, PasswordReset, PetStoreClient}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import typings.materialUiCore.components.{Button, Card, CardContent, CardHeader, TextField}
import typings.materialUiCore.materialUiCoreStrings.{submit,outlined}
import typings.materialUiCore.mod.PropTypes.Margin

import scala.concurrent.ExecutionContext
import scala.language.existentials
import scala.util.Try

object PasswordResetPage {

  case class Props(router: RouterCtl[AppPage], token: UUID)

  case class State(password1: String, password2: String, isValidToken:Option[Boolean])

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
      margin(0.px, auto)
    )
  }

  class Backend($: ScalaComponent.BackendScope[Props, State]) {
    def passwordResetForm(p: Props, s: State) = {
      <.form(^.onSubmit ==> {_ => Callback(AppCircuit.dispatch(PasswordReset(p.token,s.password1))) >> p.router.set(SignInRt)},
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
          autoFocus = true,
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
          //autoComplete = "current-password",
          `type` = "password",
          onChange = _.withInputValue.map(text => $.modState(_.copy(password2 = text))).getOrElse(Callback.empty)
        )(),

        Button(`type` = submit, disabled = {s.password1 != s.password2})("Reset")
      )
    }

    def render(p: Props, s: State) = {
      <.div(Style.outerDiv,
        <.div(Style.innerDiv,
          s.isValidToken match {
            case Some(true) =>
              Card()(
                CardHeader()(s"Password Reset"),
                CardContent()(passwordResetForm(p,s))
              )
            case Some(false) =>
              Card()(
                CardHeader()(s"The token is not valid."),
                CardContent()(
                  <.span(p.router.link(SignInRt)("Sign In.")),
                  <.br,
                  <.span(p.router.link(RegisterRt)("Create an account."))
                )
              )
            case None =>
              Card()(
                CardContent()(s"Verifying Token"),
              )
          }
        )
      )
    }
  }

  import ExecutionContext.Implicits.global

  val component = ScalaComponent.builder[Props]("PasswordReset")
    // create and store the connect proxy in state for later use
    .initialState(State("","",None))
    .renderBackend[Backend]
    .componentDidMount { $ =>
      // This should be a callback, but oh well.
      Callback.future(
        PetStoreClient
        .validateResetToken($.props.token)
        .transform { t =>
          if (t.isFailure)
            logger.log.error("Failed.", t.failed.get.asInstanceOf[Exception])
          Try($.modState(_.copy(isValidToken = Some(t.isSuccess))))
        }
      )
    }
    .build

  // create the React component for Dashboard
  def apply(router: RouterCtl[AppPage], token: UUID) = component(Props(router, token))
}
