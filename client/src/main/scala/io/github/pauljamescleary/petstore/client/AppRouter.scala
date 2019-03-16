package io.github.pauljamescleary.petstore.client

import java.util.UUID

import io.github.pauljamescleary.petstore.client.components.Footer
import io.github.pauljamescleary.petstore.client.pages._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import services._

object AppRouter {
  // Define the page routes used in the Petstore
  sealed trait AppPage
  case object HomePageRt extends AppPage
  case object SignInRt extends AppPage
  case object SignOutRt extends AppPage
  case object RegisterRt extends AppPage
  case object RecoveryRt extends AppPage
  case class PasswordResetRt(token:UUID) extends AppPage

  val userProfileWrapper = AppCircuit.connect(_.userProfile)

  def layout(c: RouterCtl[AppPage], r: Resolution[AppPage]) =
    <.div(
      userProfileWrapper(AppMenu(_, r.page, c)),
      r.render(),
      Footer()
    )

  // configure the router
  val routerConfig = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._

    val rootModelWrapper = AppCircuit.connect(x => x)

    // wrap/connect components to the circuit
    (staticRoute("#/home", HomePageRt) ~> renderR(ctl => rootModelWrapper(HomePage(ctl,_)))
      | staticRoute("#/sign-in", SignInRt) ~> renderR(ctl => userProfileWrapper(SignInPage(ctl,_)))
      | staticRoute("#/sign-out", SignOutRt) ~> renderR(ctl => userProfileWrapper(SignOutPage(ctl,_)))
      | staticRoute("#/register", RegisterRt) ~> renderR(ctl => userProfileWrapper(RegistrationPage(ctl,_)))
      | staticRoute("#/recovery", RecoveryRt) ~> renderR(RecoveryPage(_))
      | dynamicRouteCT("#/password-reset" / uuid.caseClass[PasswordResetRt]) ~> dynRenderR((rt, ctl) => PasswordResetPage(ctl,rt.token))
      | emptyRule
      ).notFound(redirectToPage(SignInRt)(Redirect.Replace))
      .renderWith(layout)
  }

  // create the router
  val router: Router[AppPage] = Router(BaseUrl.until_#, routerConfig)
}
