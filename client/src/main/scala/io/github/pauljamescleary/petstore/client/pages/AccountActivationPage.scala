package io.github.pauljamescleary.petstore.client.pages

import io.github.littlenag.scalajs.components.reactbootstrap.{Card, CardBody, CardTitle}
import io.github.pauljamescleary.petstore.client.AppRouter.{AppPage, RegisterRt, SignInRt}
import io.github.pauljamescleary.petstore.client._
import io.github.pauljamescleary.petstore.client.css.GlobalStyles
import io.github.pauljamescleary.petstore.client.services.PetStoreClient
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}

import scala.concurrent.ExecutionContext
import scala.language.existentials
import scala.util.Try

object AccountActivationPage {

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[AppPage], token: String)

  case class State(isValidToken:Option[Boolean], submitted: Boolean)

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
  }

  class Backend($: BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      <.div(Style.outerDiv,
        <.div(Style.innerDiv,
          s.isValidToken match {
            case Some(true) =>
              Card()(
                CardBody()(
                  CardTitle()("Your account has been activated."),
                  <.span(p.router.link(SignInRt)("Sign in."))
                )
              )
            case Some(false) =>
              Card()(
                CardBody()(
                  CardTitle()("The token is not valid or has expired."),
                  "To generate a new activation token: first sign in, you will then be prompted with an option to re-send your activation email.",
                  <.br,<.br,
                  "Please note that accounts not activated within NN days are automatically removed.",
                  <.br,<br,
                  <.span(p.router.link(SignInRt)("Sign in.")),
                  <.br,
                  <.span(p.router.link(RegisterRt)("Create an account.")),
                )
              )
            case None =>
              Card()(
                CardBody()(s"Verifying Token"),
              )
          }
        )
      )
    }
  }

  import ExecutionContext.Implicits.global

  val component = ScalaComponent.builder[Props]("PasswordReset")
    // create and store the connect proxy in state for later use
    .initialState(State(None,false))
    .renderBackend[Backend]
    .componentDidMount { $ =>
      // This should be a callback, but oh well.
      Callback.future(
        PetStoreClient
        .activateAccount($.props.token)
        .transform { t =>
          if (t.isFailure)
            logger.log.error("Failed.", t.failed.get.asInstanceOf[Exception])
          Try($.modState(_.copy(isValidToken = Some(t.isSuccess))))
        }
      )
    }
    .build

  // create the React component for Dashboard
  def apply(router: RouterCtl[AppPage], token: String) = component(Props(router, token))
}
