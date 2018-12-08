package io.github.pauljamescleary.petstore.frontend

import io.github.pauljamescleary.petstore.frontend.pages.Home
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import services._

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object AppRouter {
  // Define the page routes used in the Petstore
  sealed trait AppPages
  case object HomePageRt extends AppPages
  case object SignInRt extends AppPages
  case class SignOutRt(dt:String) extends AppPages

  // base layout for all pages
  def layout(c: RouterCtl[AppPages], r: Resolution[AppPages]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "Petstore")),
          <.div(^.className := "collapse navbar-collapse")
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  // configure the router
  val routerConfig = RouterConfigDsl[AppPages].buildConfig { dsl =>
    import dsl._

    //val petWrapper = AppCircuit.connect(_.pets)

    // wrap/connect components to the circuit
    (staticRoute("#/home", HomePageRt) ~> renderR(ctl => AppCircuit.wrap(_.motd)(proxy => Home(ctl, proxy)))
        | emptyRule
        ).notFound(redirectToPage(HomePageRt)(Redirect.Replace))
        .renderWith(layout)
  }

  // create the router
  val router: Router[AppPages] = Router(BaseUrl.until_#, routerConfig)
}
