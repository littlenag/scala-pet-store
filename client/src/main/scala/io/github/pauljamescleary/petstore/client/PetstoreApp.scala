package io.github.pauljamescleary.petstore.client

import com.karasiq.bootstrap.jquery.BootstrapJQueryContext
import css.{AppCSS, FontAwesomeCss}
import io.github.pauljamescleary.petstore.client.bootstrap.ReactBootstrap
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

/**
 * Tuturial WebApp entry point
 */
@JSExportTopLevel("PetstoreApp")
object PetstoreApp {

  @native
  @JSImport("bootstrap/dist/css/bootstrap.css", JSImport.Namespace)
  object BootstrapCss extends js.Object

  def main(args: Array[String]): Unit = {
    println("Hello from the Petstore!")

    BootstrapJQueryContext.useNpmImports()
    BootstrapCss

    js.Dynamic.global.ReactBootstrap = ReactBootstrap

    FontAwesomeCss

    // create stylesheet
    AppCSS.load

    // tell React to render the router in the document body
    AppRouter.router().renderIntoDOM(dom.document.getElementById("root"))
  }
}