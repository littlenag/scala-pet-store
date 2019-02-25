package io.github.pauljamescleary.petstore.frontend

import css.AppCSS
import org.scalajs.dom
import scala.scalajs.js.annotation.JSExportTopLevel

/**
 * Tuturial WebApp entry point
 */
@JSExportTopLevel("PetstoreApp")
object PetstoreApp {

  def main(args: Array[String]): Unit = {
    println("Hello from the Petstore!")

    // create stylesheet
    AppCSS.load

    // tell React to render the router in the document body
    AppRouter.router().renderIntoDOM(dom.document.getElementById("root"))
  }
}