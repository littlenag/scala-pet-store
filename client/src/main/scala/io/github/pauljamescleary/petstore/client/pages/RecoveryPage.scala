package io.github.pauljamescleary.petstore.client.pages

import typings.materialUiCore.components.{Button, Card, CardContent, TextField}
import io.github.pauljamescleary.petstore.client.AppRouter.AppPage
import io.github.pauljamescleary.petstore.client._
import io.github.pauljamescleary.petstore.client.services.{AppCircuit, PasswordRecovery}
import io.github.pauljamescleary.petstore.client.util._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, _}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import typings.materialUiCore.materialUiCoreStrings.{outlined, submit}
import typings.materialUiCore.mod.PropTypes.Margin

object RecoveryPage {

  case class Props(router: RouterCtl[AppPage])

  case class State(email:String,submitted:Boolean)

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

  class Backend($: ScalaComponent.BackendScope[Props, State]) {
    def emailForm(p: Props, s: State) = {
      <.form(^.onSubmit ==> {ev => $.modState(_.copy(submitted = true)) >> Callback(AppCircuit.dispatch(PasswordRecovery(s.email)))},
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
        Button(`type` = submit, disabled = {s.email.isEmpty})("Send Email")
      )
    }

    def render(p: Props, s: State) = {
      <.div(
      <.div(Style.outerDiv,
        <.div(Style.innerDiv,
          Card()(
            if (!s.submitted) {
              CardContent()(emailForm(p,s))
            } else {
              CardContent()(<.div("An email with a recovery link should arrive in the next few minutes."))
            }
          )
        )
      )
      )
    }
  }

  // create the React component for Dashboard

  def apply(router: RouterCtl[AppPage]) = {
    val component = ScalaComponent.builder[Props]("PasswordRecovery")
      // create and store the connect proxy in state for later use
      .initialState(State("",false))
      .renderBackend[Backend]
      .build

    component(Props(router))
  }
}
