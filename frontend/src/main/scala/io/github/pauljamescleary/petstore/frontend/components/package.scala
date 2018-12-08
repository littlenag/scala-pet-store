package io.github.pauljamescleary.petstore.frontend

import org.querki.jquery._

package object components {
  // expose jQuery under a more familiar name
  val jQuery = JQueryStatic

  val CssSettings = scalacss.devOrProdDefaults
}
