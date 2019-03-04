package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.WithProps

import scala.scalajs.js

/**
  * Bridge to [ReactBootstrap](http://react-bootstrap.github.io/)'s Badge component
  */
object Badge extends ReactBootstrapComponent {
  def apply(pill: js.UndefOr[Boolean] = js.undefined,
            variant: js.UndefOr[String] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}
