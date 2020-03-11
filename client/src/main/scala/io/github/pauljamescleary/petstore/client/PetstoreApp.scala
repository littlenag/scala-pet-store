package io.github.pauljamescleary.petstore.client

import com.karasiq.bootstrap.jquery.BootstrapJQueryContext
import css.AppCSS
import io.github.pauljamescleary.petstore.client.bootstrap.ReactBootstrap
import io.github.pauljamescleary.petstore.client.img.FontAwesomeCss
import io.github.pauljamescleary.petstore.client.logger._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

/**
 * Pet Store entry point
 */
@JSExportTopLevel("PetstoreApp")
object PetstoreApp {

  object imports {
    @js.native
    @JSImport("bootstrap/dist/css/bootstrap.css", JSImport.Namespace)
    object BootstrapCssImport extends js.Object


  }

  def main(args: Array[String]): Unit = {
    // Ensure we have logging
    log4javascript

    // Load FontAwesome (nice fonts!)
    FontAwesomeCss

    // Load jQuery, then init both the bootstrap javascript and css
    BootstrapJQueryContext.useNpmImports()
    imports.BootstrapCssImport

    // Load React and React-Bootstrap
    ReactBootstrap

    ///
    /// At this point all our app deps have been loaded and we good to start initializing
    /// our application itself.
    ///

    log.debug("Welcome to the Scala Pet Store!")

    // create stylesheet
    AppCSS.load

    // tell React to render the router in the document body
    AppRouter.router().renderIntoDOM(dom.document.getElementById("root"))
  }
}