package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.ReactBridgeComponent

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Common class for all [ReactBootstrap](http://react-bootstrap.github.io/)'s components
  */
abstract class ReactBootstrapComponent extends ReactBridgeComponent {
  // ReactBootstrap = require("react-bootstrap");
  override lazy val componentNamespace: String = "ReactBootstrap"
}

@native
@JSImport("react-bootstrap", JSImport.Namespace)
object ReactBootstrap extends js.Object