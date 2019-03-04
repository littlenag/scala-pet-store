package io.github.pauljamescleary.petstore.client

import com.karasiq.bootstrap.jquery.BootstrapJQueryContext
import css.AppCSS
import io.github.pauljamescleary.petstore.client.bootstrap.ReactBootstrap
import io.github.pauljamescleary.petstore.client.img.FontAwesomeCss
import io.github.pauljamescleary.petstore.client.logger.Log4JavaScript
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

/**
 * Tuturial WebApp entry point
 */
@JSExportTopLevel("PetstoreApp")
object PetstoreApp {

  @js.native
  @JSImport("bootstrap/dist/css/bootstrap.css", JSImport.Namespace)
  object BootstrapCss extends js.Object

  @js.native
  @JSImport("log4javascript", JSImport.Namespace)
  object log4javascript extends Log4JavaScript

  def main(args: Array[String]): Unit = {
    println("Hello from the Petstore!")

    FontAwesomeCss

    //js.Dynamic.global.log4javascript = log4javascript
    log4javascript

    BootstrapJQueryContext.useNpmImports()
    BootstrapCss

    ReactBootstrap

    // create stylesheet
    AppCSS.load

    // tell React to render the router in the document body
    AppRouter.router().renderIntoDOM(dom.document.getElementById("root"))
  }
}