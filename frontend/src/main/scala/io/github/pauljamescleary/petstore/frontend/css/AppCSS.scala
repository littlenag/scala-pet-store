package io.github.pauljamescleary.petstore.frontend.css

import scalacss.internal.mutable.GlobalRegistry
//import scalajsreact.template.components.{LeftNav, TopNav}
//import scalajsreact.template.pages.{HomePage, ItemsPage}

object AppCSS {

  import CssSettings._

  def load = {
    GlobalRegistry.register(GlobalStyles)
    GlobalRegistry.register(GlobalStyles.bootstrapStyles)
    GlobalRegistry.onRegistration(_.addToDocument())
  }
}
