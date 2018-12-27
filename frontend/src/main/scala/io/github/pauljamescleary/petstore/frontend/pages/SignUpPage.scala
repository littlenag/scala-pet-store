package io.github.pauljamescleary.petstore.frontend.pages

import diode.react.ReactPot._
import diode.data.Pot
import diode.data.PotState.PotEmpty
import diode.react._
import io.github.pauljamescleary.petstore.frontend.AppRouter.{AppPage, HomePageRt}
import io.github.pauljamescleary.petstore.frontend._
import io.github.pauljamescleary.petstore.frontend.css.Bootstrap.Panel
import io.github.pauljamescleary.petstore.frontend.css.GlobalStyles
import io.github.pauljamescleary.petstore.frontend.services.{SignIn, SignUp, UserProfile}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}

import scala.language.existentials

object SignUpPage {

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[AppPage], userProfile: ModelProxy[Pot[UserProfile]])

  case class State(username: String, email:String, password: String)

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

  // create the React component for Dashboard
  private val component = ScalaComponent.builder[Props]("SignUp")
      // create and store the connect proxy in state for later use
      .initialState(State("", "", ""))
      .renderPS { (b, p, s) =>
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
                Panel(Panel.Props(s"Sign Up -- There was an error: ${ex.getMessage}"),
                  <.form(^.onSubmit ==> {ev => p.userProfile.dispatchCB(SignUp(s.username, s.email, s.password))},
                    <.div(bss.formGroup,
                      <.label(^.`for` := "description", "Username"),
                      <.input.text(bss.formControl,
                        ^.id := "username",
                        ^.value := s.username,
                        ^.placeholder := "Username",
                        ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; b.modState(_.copy(username = text))}
                      )
                    ),
                    <.div(bss.formGroup,
                      <.label(^.`for` := "description", "Email"),
                      <.input.text(bss.formControl,
                        ^.id := "email",
                        ^.value := s.email,
                        ^.placeholder := "Email",
                        ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; b.modState(_.copy(email = text))}
                      )
                    ),
                    <.div(bss.formGroup,
                      <.label(^.`for` := "description", "Password"),
                      <.input.text(bss.formControl,
                        ^.id := "password",
                        ^.value := s.password,
                        ^.placeholder := "Password",
                        ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; b.modState(_.copy(password = text))}
                      )
                    ),
                    <.button("Submit")
                  )
                )
              )
            )
          },
          // Pending is conflated with Empty, so test state instead
          if (p.userProfile().state == PotEmpty) {
            <.div(Style.outerDiv,
              <.div(Style.innerDiv,
                Panel(Panel.Props("Sign Up"),
                  <.form(^.onSubmit ==> {ev => p.userProfile.dispatchCB(SignUp(s.username, s.email, s.password))},
                    <.div(bss.formGroup,
                      <.label(^.`for` := "description", "Username"),
                      <.input.text(bss.formControl,
                        ^.id := "username",
                        ^.value := s.username,
                        ^.placeholder := "Username",
                        ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; b.modState(_.copy(username = text))}
                      )
                    ),
                    <.div(bss.formGroup,
                      <.label(^.`for` := "description", "Email"),
                      <.input.text(bss.formControl,
                        ^.id := "email",
                        ^.value := s.email,
                        ^.placeholder := "Email",
                        ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; b.modState(_.copy(email = text))}
                      )
                    ),
                    <.div(bss.formGroup,
                      <.label(^.`for` := "description", "Password"),
                      <.input.text(bss.formControl,
                        ^.id := "password",
                        ^.value := s.password,
                        ^.placeholder := "Password",
                        ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; b.modState(_.copy(password = text))}
                      )
                    ),
                    <.button("Submit")
                  )
                )
              )
            )
          }
          else EmptyVdom
        )
      }
      .build

  def apply(router: RouterCtl[AppPage], proxy: ModelProxy[Pot[UserProfile]]) = component(Props(router, proxy))
}
