package io.github.pauljamescleary.petstore.client.pages

import io.github.pauljamescleary.petstore.client.AppRouter.AppPage
import io.github.pauljamescleary.petstore.client._
import io.github.littlenag.scalajs.components.reactbootstrap.{Card, CardBody}
import io.github.pauljamescleary.petstore.client.css.GlobalStyles
import io.github.pauljamescleary.petstore.client.services.{AppCircuit, PasswordRecovery}
import japgolly.scalajs.react.{Callback, _}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}

object RecoveryPage {

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

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

  def emailForm($: BackendScope[Props, State], p: Props, s: State) = {
    <.form(^.onSubmit ==> {ev => $.modState(_.copy(submitted = true)) >> Callback(AppCircuit.dispatch(PasswordRecovery(s.email)))},
      <.div(bss.formGroup,
        //<.label(^.`for` := "description", "Email"),
        <.input.text(bss.formControl,
          ^.id := "email",
          ^.value := s.email,
          ^.placeholder := "Email",
          ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(email = text))}
        )
      ),
      <.button(^.disabled := {s.email.isEmpty})("Send Email")
    )
  }

  class Backend($: BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      <.div(
        <.div(Style.outerDiv,
          <.div(Style.innerDiv,
            Card()(
              if (!s.submitted) {
                CardBody()(emailForm($,p,s))
              } else {
                CardBody()(<.div("An email with a recovery link should arrive in the next few minutes."))
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
