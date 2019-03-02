package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.WithProps
import org.scalajs.dom.html.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Bridge to [ReactBootstrap](http://react-bootstrap.github.io/)'s Modal components
  */
object Modal extends ReactBootstrapComponent {
  def apply(animation: js.UndefOr[Boolean] = true,
            keyboard: js.UndefOr[Boolean] = true,
            show: js.UndefOr[Boolean] = false,
            onHide: js.UndefOr[js.Function0[Unit]] = js.undefined,
            onEntered: js.UndefOr[js.Function0[Unit]] = js.undefined): WithProps = auto
}

@js.native
@JSImport("react-bootstrap/Modal", JSImport.Namespace)
private object ModalObj extends js.Object {
  def Body: js.Any = js.native
  def Title: js.Any = js.native
  def Header: js.Any = js.native
  def Footer: js.Any = js.native
  def Dialog: js.Any = js.native
}

object ModalDialog extends ReactBootstrapComponent {
  def apply(centered: js.UndefOr[Boolean] = js.undefined,
            size: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

// Special handling, since react-bootstrap doesn't see the need to actually export this one, gah!
object ModalHeader extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Modal.Header"
  override protected lazy val componentValue: js.Any = ModalObj.Header
  def apply(closeButton: js.UndefOr[Boolean] = false,
            closeLabel: js.UndefOr[String] = "Close",
            onHide: js.UndefOr[js.Function0[Unit]] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object ModalTitle extends ReactBootstrapComponent {
  def apply(as: js.UndefOr[Element] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object ModalBody extends ReactBootstrapComponent {
  def apply(as: js.UndefOr[Element] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object ModalFooter extends ReactBootstrapComponent {
  def apply(as: js.UndefOr[Element] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}
