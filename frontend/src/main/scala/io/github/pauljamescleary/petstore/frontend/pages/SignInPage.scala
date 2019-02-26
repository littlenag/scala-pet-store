package io.github.pauljamescleary.petstore.frontend.pages

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import io.github.pauljamescleary.petstore.frontend._
import AppRouter.{AppPage, HomePageRt, SignUpRt}
import diode.data.PotState.PotEmpty
import io.github.pauljamescleary.petstore.frontend.css.Bootstrap.Panel
import io.github.pauljamescleary.petstore.frontend.css.GlobalStyles
import io.github.pauljamescleary.petstore.frontend.services.SignIn
import io.github.pauljamescleary.petstore.frontend.services.UserProfile

import scala.language.existentials

object SignInPage {

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[AppPage], userProfile: ModelProxy[Pot[UserProfile]])

  case class State(username: String, password: String)

  import css.CssSettings._
  import scalacss.ScalaCssReact._

  object Style extends StyleSheet.Inline {
    import dsl._

    val outerDiv = style(textAlign.center,
      alignItems.flexStart,
      paddingTop(120.px),
      display.flex,
      flexDirection.column
    )

    val innerDiv = style(textAlign.center,
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

  class Backend($: BackendScope[Props, State]) {

    def signInForm(p: Props, s: State) = {
      <.form(^.onSubmit ==> { ev => p.userProfile.dispatchCB(SignIn(s.username, s.password)) },
        <.div(bss.formGroup,
          <.label(^.`for` := "description", "Username"),
          <.input.text(bss.formControl,
            ^.id := "username",
            ^.value := s.username,
            ^.placeholder := "Username",
            ^.onChange ==> { ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(username = text)) }
          )
        ),
        <.div(bss.formGroup,
          <.label(^.`for` := "description", "Password"),
          <.input.text(bss.formControl,
            ^.id := "password",
            ^.value := s.password,
            ^.`type` := "password",
            ^.placeholder := "Password",
            ^.onChange ==> { ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(password = text)) }
          )
        ),
        <.button("Submit"),
        <.div(
          Style.links,
          <.span(p.router.link(SignUpRt)("Create an account."))
        )
      )
    }

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
              Panel(Panel.Props("Sign In -- Failed!"), signInForm(p,s))
            )
          )
        },
        // Pending is conflated with Empty, so test state instead
        if (p.userProfile().state == PotEmpty) {
          <.div(Style.outerDiv,
            <.div(Style.innerDiv,
              Panel(Panel.Props("Sign In"), signInForm(p,s))
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
