package io.github.pauljamescleary.petstore.client.css

import io.github.pauljamescleary.petstore.client.pages.{HomePage, SignInPage, RegistrationPage, AppMenu}
import scalacss.internal.mutable.GlobalRegistry

object AppCSS {

  import CssSettings._

  def load = {
    GlobalRegistry.register(
      GlobalStyles,
      SignInPage.Style,
      RegistrationPage.Style,
      HomePage.Style,
      AppMenu.Style)
    GlobalRegistry.register(GlobalStyles.bootstrapStyles)
    GlobalRegistry.onRegistration(_.addToDocument())
  }
}
