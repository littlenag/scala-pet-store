package reactjs

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.Callback
import ngage.JSWriterGenerator

object Welcome extends ReactBridgeComponent with JSWriterGenerator {
  override lazy val componentNamespace: String = "react_ui"
  implicit val writer = jsWriter[TestClass]

  def apply(name: String,
            profile: TestClass,
            updateField: TestClass => Callback): WithProps = auto
}
