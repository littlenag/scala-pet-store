package reactjs
import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js

object JssProvider extends ReactBridgeComponent {
  override lazy val componentNamespace: String = "react_ui"

  def apply(classNamePrefix: js.UndefOr[String] = js.undefined): WithProps =
    auto
}
