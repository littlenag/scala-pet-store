package io.github.pauljamescleary.petstore.client.pages

import diode.react.ReactPot._
import diode.data.Pot
import diode.data.PotState.PotEmpty
import diode.react._
import io.github.pauljamescleary.petstore.client.AppRouter.{AppPage, HomePageRt, SignInRt}
import io.github.pauljamescleary.petstore.client._
import io.github.pauljamescleary.petstore.client.css.Bootstrap.Panel
import io.github.pauljamescleary.petstore.client.css.GlobalStyles
import io.github.pauljamescleary.petstore.client.services.{SignUp, UserProfile}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}

import scala.language.existentials

object SignUpPage {

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

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

  def signUpForm($: BackendScope[Props, State], p: Props, s: State) = {
    <.form(^.onSubmit ==> {ev => p.userProfile.dispatchCB(SignUp(s.username, s.email, s.password1))},
      <.div(bss.formGroup,
        <.label(^.`for` := "description", "Username"),
        <.input.text(bss.formControl,
          ^.id := "username",
          ^.value := s.username,
          ^.placeholder := "Username",
          ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(username = text))}
        )
      ),
      <.div(bss.formGroup,
        <.label(^.`for` := "description", "Email"),
        <.input.text(bss.formControl,
          ^.id := "email",
          ^.value := s.email,
          ^.placeholder := "Email",
          ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(email = text))}
        )
      ),
      <.div(bss.formGroup,
        <.label(^.`for` := "description", "Password"),
        <.input.text(bss.formControl,
          ^.id := "password",
          ^.value := s.password1,
          ^.`type` := "password",
          ^.placeholder := "Password",
          ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(password1 = text))}
        )
      ),
      <.div(bss.formGroup,
        <.label(^.`for` := "description", "Confirm Password"),
        <.input.text(bss.formControl,
          ^.id := "password2",
          ^.value := s.password2,
          ^.`type` := "password",
          ^.placeholder := "Confirm Password",
          ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(password2 = text))}
        )
      ),
      <.button(^.disabled := {s.password1 != s.password2})("Submit"),
      <.div(
        Style.links,
        <.span("Already a member? ", p.router.link(SignInRt)("Sign in now"))
      )

    )
  }

  class Backend($: BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      <.div(
        p.userProfile().renderReady { up =>
          // This seems to be the easy way to redirect. but have to make sure to run AFTER rendering!
          p.router.set(HomePageRt).async.runNow()
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
              Panel(Panel.Props(s"Sign Up -- There was an error: ${ex.getMessage}"),signUpForm($,p,s))
            )
          )
        },
        // Pending is conflated with Empty, so test state instead
        if (p.userProfile().state == PotEmpty) {
          <.div(Style.outerDiv,
            <.div(Style.innerDiv,
              Panel(Panel.Props("Sign Up"),signUpForm($,p,s))
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
