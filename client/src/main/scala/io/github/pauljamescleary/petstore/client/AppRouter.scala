package io.github.pauljamescleary.petstore.client

import io.github.pauljamescleary.petstore.client.components.Footer
import io.github.pauljamescleary.petstore.client.pages.{AppMenu, HomePage, SignInPage, SignUpPage}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import services._

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object AppRouter {
  // Define the page routes used in the Petstore
  sealed trait AppPage
  case object HomePageRt extends AppPage
  case object SignInRt extends AppPage
  case object SignUpRt extends AppPage
  case object LogOutRt extends AppPage

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

    //val petWrapper = AppCircuit.connect(_.pets)

    val rootModelWrapper = AppCircuit.connect(x => x)

    // wrap/connect components to the circuit
    (staticRoute("#/home", HomePageRt) ~> renderR(ctl => rootModelWrapper(HomePage(ctl,_)))
        | staticRoute("#/sign-in", SignInRt) ~> renderR(ctl => userProfileWrapper(SignInPage(ctl,_)))
        | staticRoute("#/sign-up", SignUpRt) ~> renderR(ctl => userProfileWrapper(SignUpPage(ctl,_)))
        | emptyRule
        ).notFound(redirectToPage(SignInRt)(Redirect.Replace))
        .renderWith(layout)
  }

  // create the router
  val router: Router[AppPage] = Router(BaseUrl.until_#, routerConfig)
}
