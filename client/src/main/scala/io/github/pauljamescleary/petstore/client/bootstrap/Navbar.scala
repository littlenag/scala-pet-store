package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.WithProps
import org.scalajs.dom.html.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

/**
  * Bridge to [ReactBootstrap](http://react-bootstrap.github.io/)'s Navbar components
  */
object Navbar extends ReactBootstrapComponent {
  def apply(bg: js.UndefOr[String] = js.undefined,
            fixed: js.UndefOr[String] = js.undefined,
            varient: js.UndefOr[String] = js.undefined,
            sticky : js.UndefOr[String] = js.undefined,
            expand : js.UndefOr[String | Boolean] = js.undefined,
            expanded : js.UndefOr[Boolean] = js.undefined,
            onSelect: js.UndefOr[js.Function0[Unit]] = js.undefined,
            onToggle: js.UndefOr[js.Function0[Unit]] = js.undefined): WithProps = auto
}

@js.native
@JSImport("react-bootstrap/Navbar", JSImport.Namespace)
private object NavbarObj extends js.Object {
  //def Brand: js.Any = js.native
  def Toggle: js.Any = js.native
  def Collapse: js.Any = js.native
  def Text: js.Any = js.native
}

object NavbarBrand extends ReactBootstrapComponent {
  def apply(as: js.UndefOr[Element] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

// Special handling, since react-bootstrap doesn't see the need to actually export this one, gah!
object NavbarToggle extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Navbar.Toggle"
  override protected lazy val componentValue: js.Any = NavbarObj.Toggle
  def apply(as: js.UndefOr[Element] = js.undefined,
            label: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object NavbarCollapse extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Navbar.Collapse"
  override protected lazy val componentValue: js.Any = NavbarObj.Collapse
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object NavbarText extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Navbar.Text"
  override protected lazy val componentValue: js.Any = NavbarObj.Text
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}