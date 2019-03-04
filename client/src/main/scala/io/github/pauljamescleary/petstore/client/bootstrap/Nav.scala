package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.WithProps
import japgolly.scalajs.react.vdom.VdomElement
import org.scalajs.dom.html.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Bridge to [ReactBootstrap](http://react-bootstrap.github.io/)'s Nav components
  */
object Nav extends ReactBootstrapComponent {
  def apply(activeKey: js.UndefOr[String] = js.undefined,
            as: js.UndefOr[Element] = js.undefined,
            fill: js.UndefOr[Boolean] = js.undefined,
            justify: js.UndefOr[Boolean] = js.undefined,
            navbar: js.UndefOr[Boolean] = js.undefined,
            role: js.UndefOr[String] = js.undefined,
            variant: js.UndefOr[String] = js.undefined,
            onSelect: js.UndefOr[js.Function0[Unit]] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object NavItem extends ReactBootstrapComponent {
  def apply(as: js.UndefOr[Element] = js.undefined,
            role: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

@js.native
@JSImport("react-bootstrap/Nav", JSImport.Namespace)
private object NavObj extends js.Object {
  def Link: js.Any = js.native
}

object NavLink extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Nav.Link"
  override protected lazy val componentValue: js.Any = NavObj.Link
  def apply(active: js.UndefOr[Boolean] = js.undefined,
            as: js.UndefOr[Element] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            eventKey: js.UndefOr[js.Any] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            onSelect: js.UndefOr[js.Function0[Unit]] = js.undefined,
            role: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

// Special handling, since react-bootstrap doesn't see the need to actually export this one, gah!
object NavDropdown extends ReactBootstrapComponent {
  def apply(title: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

@js.native
@JSImport("react-bootstrap/NavDropdown", JSImport.Namespace)
private object NavDropdownObj extends js.Object {
  def Item: js.Any = js.native
  def Divider: js.Any = js.native
}

// Special handling, since react-bootstrap doesn't see the need to actually export this one, gah!
object NavDropdownItem extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "NavDropdown.Item"
  override protected lazy val componentValue: js.Any = NavDropdownObj.Item
  def apply(href: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

// Special handling, since react-bootstrap doesn't see the need to actually export this one, gah!
object NavDropdownDivider extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "NavDropdown.Divider"
  override protected lazy val componentValue: js.Any = NavDropdownObj.Divider
  def apply(): VdomElement = autoNoTagModsNoChildren
}