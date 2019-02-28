package ngage

import com.payalabs.scalajs.react.bridge.{JsWriter, writerFromConversion}

import scala.reflect.macros.blackbox
import scala.scalajs.js
import scala.language.experimental.macros

object Macros {

  def jsWriterForType[A: c.WeakTypeTag, J <: JsWriter[_] : c.WeakTypeTag](c: blackbox.Context): c.Expr[JsWriter[A]] = {
    import c.universe._

    val typeTag = c.weakTypeOf[A]

    def computeParams: c.Expr[List[(String, js.Any)]] = {

      val pairs: c.Expr[List[(String, js.Any)]] = {
        val convertedProps = typeTag.decls.collect {
          case m: MethodSymbol if m.isCaseAccessor  =>
            val paramType = m.typeSignature.resultType
            val converted = {
              val conv = c.inferImplicitValue(appliedType(weakTypeOf[J], paramType :: Nil))
              q"$conv.toJs(value.${m.name.toTermName})"
            }
            (m.name.toString, converted)
          case m:MethodSymbol if m.isMethod && m.name.toString.startsWith("with") => //Expose only withXXX case class's functions
            val converted = {
              q"value.${m.name.toTermName} _"
            }
            (m.name.toString, converted)
        }.toList
        c.Expr[List[(String, js.Any)]](q"$convertedProps")
      }
      pairs
    }

    c.Expr(
      q"""
        new _root_.com.payalabs.scalajs.react.bridge.JsWriter[${implicitly[WeakTypeTag[A]].tpe}] {
          override def toJs(value: ${implicitly[WeakTypeTag[A]].tpe}): _root_.scala.scalajs.js.Any = {
            _root_.com.payalabs.scalajs.react.bridge.ReactBridgeComponent.propsToDynamic(${computeParams})
          }

        }
       """)
  }

}

trait JSWriterGenerator {
  implicit def longWriter: JsWriter[Long] = writerFromConversion[Long]

  def jsWriter[A]: JsWriter[A] = macro Macros.jsWriterForType[A, JsWriter[_]]

  implicit def setWriter[A](implicit A: JsWriter[A]): JsWriter[Set[A]] = {
    JsWriter((value: Set[A]) => js.Array(value.map(e => A.toJs(e)).toList: _*))
  }

}
