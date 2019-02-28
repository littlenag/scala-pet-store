package reactjs
import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.ReactEvent
import scala.scalajs.js

object Button extends ReactBridgeComponent {
  override lazy val componentNamespace: String = "mui"

  def apply(
            active: js.UndefOr[Boolean] = js.undefined,
            block: js.UndefOr[Boolean] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            className: js.UndefOr[String] = js.undefined,
            color: js.UndefOr[String] = js.undefined,
            onClick: js.UndefOr[ReactEvent => Callback] = js.undefined): WithProps = auto
}
