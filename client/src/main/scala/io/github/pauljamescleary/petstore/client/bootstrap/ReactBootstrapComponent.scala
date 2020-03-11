package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.ReactBridgeComponent

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Common class for all [ReactBootstrap](http://react-bootstrap.github.io/)'s components.
  *
  * https://github.com/ochrons/scalajs-spa-tutorial
  *
  */
abstract class ReactBootstrapComponent extends ReactBridgeComponent {
  override lazy val componentNamespace: String = "ReactBootstrap"
}

object ReactBootstrap {
  object imports {
    @js.native
    @JSImport("react", JSImport.Namespace)
    object React extends js.Object

    @js.native
    @JSImport("react-bootstrap", JSImport.Namespace)
    object ReactBootstrapGlobal extends js.Object
  }

  def useNpmImports() = {
    imports.React

    // In order for ReactBootstrapComponent to work, the object needs to be in global scope.
    js.Dynamic.global.ReactBootstrap = imports.ReactBootstrapGlobal
  }

}