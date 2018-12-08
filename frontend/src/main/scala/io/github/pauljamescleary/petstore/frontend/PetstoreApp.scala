package io.github.pauljamescleary.petstore.frontend

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExportTopLevel
//import dom.ext.Ajax

//import scala.concurrent.ExecutionContext.Implicits.global
//import cats.implicits._

import io.github.pauljamescleary.petstore._
//import shared.domain.pets._
//import components._
import io.github.pauljamescleary.petstore.frontend.styling.GlobalStyles
import services._
import pages._

import styling.CssSettings._
import scalacss.ScalaCssReact._


/**
 * Tuturial WebApp entry point
 */
@JSExportTopLevel("PetstoreApp")
object PetstoreApp {

  def main(args: Array[String]): Unit = {
    println("Hello from the Petstore!")

    // create stylesheet
    GlobalStyles.addToDocument()

    // tell React to render the router in the document body
    AppRouter.router().renderIntoDOM(dom.document.getElementById("root"))
  }
}