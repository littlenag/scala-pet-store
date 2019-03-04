package io.github.pauljamescleary.petstore.client.css

import japgolly.univeq.UnivEq
import scalacss.internal.mutable

import CssSettings._

// Common Bootstrap contextual styles
object CommonStyle extends Enumeration {
  val default, primary, success, info, warning, danger = Value
}

class BootstrapCss(implicit r: mutable.Register) extends StyleSheet.Inline()(r) {

  import dsl._

  import CommonStyle._

  implicit val styleUnivEq: UnivEq[CommonStyle.Value] = new UnivEq[CommonStyle.Value] {}

  val csDomain = Domain.ofValues(default, primary, success, info, warning, danger)

  val contextDomain = Domain.ofValues(success, info, warning, danger)

  def commonStyle[A: UnivEq](domain: Domain[A], base: String) = styleF(domain)(opt =>
    styleS(addClassNames(base, s"$base-$opt"))
  )

  def styleWrap(classNames: String*): StyleA = style(addClassNames(classNames: _*))

  val buttonOpt = commonStyle(csDomain, "btn")

  val button = buttonOpt(default)

  val panelOpt = commonStyle(csDomain, "panel")

  val panel = panelOpt(default)

  val labelOpt = commonStyle(csDomain, "label")

  val label = labelOpt(default)

  val alert = commonStyle(contextDomain, "alert")

  val panelHeading = styleWrap("panel-heading")

  val panelBody = styleWrap("panel-body")

  // wrap styles in a namespace, assign to val to prevent lazy initialization
  object modal {
    val modal = styleWrap("modal")
    val fade = styleWrap("fade")
    val dialog = styleWrap("modal-dialog")
    val content = styleWrap("modal-content")
    val header = styleWrap("modal-header")
    val body = styleWrap("modal-body")
    val footer = styleWrap("modal-footer")
  }

  val _modal = modal

  object equalWidth {
    val container = styleWrap("container")
    val row = styleWrap("row")
    val col = styleWrap("col")
  }

  val floatRight = styleWrap("float-right")
  val floatLeft = styleWrap("float-left")
  val buttonXS = styleWrap("btn-xs")
  val close = styleWrap("close")

  val labelAsBadge = style(addClassName("label-as-badge"), borderRadius(1.em))

  val navbar = styleWrap("navbar", "navbar-expand-lg", "navbar-dark", "bg-dark", "fixed-top")

  val formGroup = styleWrap("form-group")
  val formControl = styleWrap("form-control")
}
