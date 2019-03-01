package io.github.pauljamescleary.petstore.client.bootstrap

import com.payalabs.scalajs.react.bridge.WithProps
import org.scalajs.dom.html.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Bridge to [ReactBootstrap](http://react-bootstrap.github.io/)'s Card components
  */
object Card extends ReactBootstrapComponent {
  def apply(as: js.UndefOr[Element] = js.undefined,
            bg: js.UndefOr[String] = js.undefined,
            body: js.UndefOr[Boolean] = false,
            border: js.UndefOr[String] = js.undefined,
            text: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

@js.native
@JSImport("react-bootstrap/Card", JSImport.Namespace)
private object CardObj extends js.Object {
  def Title: js.Any = js.native
  def Subtitle: js.Any = js.native
  def Header: js.Any = js.native
  def Footer: js.Any = js.native
  def Text: js.Any = js.native
  def Body: js.Any = js.native
  def Link: js.Any = js.native
}

object CardTitle extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Card.Title"
  override protected lazy val componentValue: js.Any = CardObj.Title
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object CardSubtitle extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Card.Subtitle"
  override protected lazy val componentValue: js.Any = CardObj.Subtitle
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object CardHeader extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Card.Header"
  override protected lazy val componentValue: js.Any = CardObj.Header
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object CardFooter extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Card.Footer"
  override protected lazy val componentValue: js.Any = CardObj.Footer
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object CardText extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Card.Text"
  override protected lazy val componentValue: js.Any = CardObj.Text
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object CardBody extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Card.Body"
  override protected lazy val componentValue: js.Any = CardObj.Body
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

object CardLink extends ReactBootstrapComponent {
  override protected lazy val componentName: String = "Card.Link"
  override protected lazy val componentValue: js.Any = CardObj.Link
  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}
