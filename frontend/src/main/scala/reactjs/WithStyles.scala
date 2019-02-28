package reactjs

import reactjs.theme.MuiRawTheme

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
trait WithStyles extends js.Object {
  def apply(styles: js.Function1[MuiRawTheme, js.Object],
            options: js.Object,
            component: js.Any): js.Any = js.native
}

@js.native
@JSGlobal("mui.createComponentWithStyle")
object WithStyles extends WithStyles