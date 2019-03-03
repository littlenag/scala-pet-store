package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.WithProps
import japgolly.scalajs.react.Callback

import scala.scalajs.js

/**
  * Bridge to [ReactBootstrap](http://react-bootstrap.github.io/)'s Button component
  */
object Button extends ReactBootstrapComponent {
  def apply(varient: js.UndefOr[String] = js.undefined,
            size: js.UndefOr[String] = js.undefined,
            active: js.UndefOr[Boolean] = js.undefined,
            block: js.UndefOr[Boolean] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            onClick: js.UndefOr[js.Function0[Unit]] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto

  def props(varient: js.UndefOr[String] = js.undefined,
            size: js.UndefOr[String] = js.undefined,
            active: js.UndefOr[Boolean] = js.undefined,
            block: js.UndefOr[Boolean] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            onClick: js.UndefOr[Callback] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = apply(varient,size,active,block,disabled,href,onClick.flatMap(_.toJsCallback),bsPrefix)
}
