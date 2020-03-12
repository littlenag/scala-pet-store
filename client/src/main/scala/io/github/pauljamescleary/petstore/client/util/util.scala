package io.github.pauljamescleary.petstore.client

import japgolly.scalajs.react.ReactEventFrom
import org.scalajs.dom.raw.{Element, HTMLInputElement, HTMLSelectElement, HTMLTextAreaElement}

import scala.scalajs.js.|

/**
 * @author Mark Kegel (mkegel@vast.com)
 */
package object util {

  implicit class InputHelperOps(val ev: ReactEventFrom[(HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement) with Element]) extends AnyVal {
    def withInputValue: Option[String] = {
      ev.target match {
        case i: HTMLInputElement => Option(new String(i.value))
        case _ => None
      }
    }
  }

}
