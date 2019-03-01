package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.WithProps
import org.scalajs.dom.html.Element

import scala.scalajs.js

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

object ModalDialog extends ReactBootstrapComponent {
  def apply(centered: js.UndefOr[Boolean] = js.undefined,
            size: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object ModalHeader extends ReactBootstrapComponent {
  def apply(closeButton: js.UndefOr[Boolean] = false,
            closeLabel: js.UndefOr[String] = "Close",
            bsPrefix: js.UndefOr[String] = js.undefined,
            onHide: js.UndefOr[js.Function0[Unit]] = js.undefined): WithProps = auto
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
