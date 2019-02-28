package reactjs
import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js

object MuiThemeProvider extends ReactBridgeComponent {
  override lazy val componentNamespace: String = "mui"

  def apply(theme: js.Object): WithProps = auto
}
