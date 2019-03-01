package io.github.pauljamescleary.petstore.client.css

import io.github.pauljamescleary.petstore.client.pages.{HomePage, SignInPage, SignUpPage, AppMenu}
import scalacss.internal.mutable.GlobalRegistry

object AppCSS {

  import CssSettings._

  def load = {
    GlobalRegistry.register(
      GlobalStyles,
      SignInPage.Style,
      SignUpPage.Style,
      HomePage.Style,
      AppMenu.Style)
    GlobalRegistry.register(GlobalStyles.bootstrapStyles)
    GlobalRegistry.onRegistration(_.addToDocument())
  }
}
