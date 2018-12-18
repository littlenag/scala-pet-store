package io.github.pauljamescleary.petstore.frontend.pages

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import io.github.pauljamescleary.petstore.frontend._
import AppRouter.{AppPage, HomePageRt}
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
      //fontSize(20.px),
      //minHeight(450.px),
      //width(400.px),
      alignItems.flexStart,
      paddingTop(120.px),
      display.flex,
      flexDirection.column
    )

    val innerDiv = style(textAlign.center,
      //fontSize(20.px),
      minHeight(450.px),
      width(400.px),
      alignItems.flexStart
      //paddingTop(120.px)
    )
  }

  class Backend($: BackendScope[Props, State]) {

    def render(p: Props, s: State) = {
      p.userProfile().renderReady { up =>
        p.router.set(HomePageRt).async.runNow()
        <.div(
          Style.outerDiv,
          up.toString
        )
      }

      p.userProfile().renderEmpty {
        <.div(Style.outerDiv,
          <.div(Style.innerDiv,
            Panel(Panel.Props("Sign In"),
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
                    ^.placeholder := "Password",
                    ^.onChange ==> { ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(password = text)) }
                  )
                ),
                <.button("Submit")
              )
            )
          )
        )
      }

      p.userProfile().renderEmpty {
        <.div(Style.outerDiv,
          <.div(Style.innerDiv,
            Panel(Panel.Props("Sign In"),
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
                    ^.placeholder := "Password",
                    ^.onChange ==> { ev: ReactEventFromInput => val text = ev.target.value; $.modState(_.copy(password = text)) }
                  )
                ),
                <.button("Submit")
              )
            )
          )
        )
      }
    }
  }

  // create the React component
  val component = ScalaComponent.builder[Props]("SignIn")
      .initialState(State("", ""))
      .renderBackend[Backend]
      .build

  def apply(router: RouterCtl[AppPage], proxy: ModelProxy[Pot[UserProfile]]) = component(Props(router, proxy))
}
