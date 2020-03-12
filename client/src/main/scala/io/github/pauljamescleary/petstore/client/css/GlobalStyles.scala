package io.github.pauljamescleary.petstore.client.css

import CssSettings._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  style(
    unsafeRoot("body")(
      margin.`0`,
      padding.`0`,
      fontSize(14.px),
      fontFamily := "Roboto, sans-serif"
    )
  )

  val colCentered = style(
    float.none,
    margin(0 px, auto)
  )
}
