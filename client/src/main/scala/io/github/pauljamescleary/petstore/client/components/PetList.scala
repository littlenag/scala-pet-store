package io.github.pauljamescleary.petstore.client.components

import io.github.pauljamescleary.petstore.client.bootstrap.Button
import io.github.pauljamescleary.petstore.domain.pets.Pet
import io.github.pauljamescleary.petstore.domain.pets.PetStatus.{Adopted, Available, Pending}
import io.github.pauljamescleary.petstore.client.css.CommonStyle
import io.github.pauljamescleary.petstore.client.css.GlobalStyles
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object PetList {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class PetListProps(
                           pets: Seq[Pet],
                           stateChange: Pet => Callback,
                           editItem: Pet => Callback,
                           deleteItem: Pet => Callback
                          )

  private val PetList = ScalaComponent.builder[PetListProps]("PetList")
    .render_P(p => {
      val style = bss.listGroup
      def renderItem(pet: Pet) = {
        // convert priority into Bootstrap style
        val itemStyle = pet.status match {
          case Available => style.itemOpt(CommonStyle.info)
          case Pending => style.item
          case Adopted => style.itemOpt(CommonStyle.danger)
        }
        <.li(itemStyle,
          <.label(
            <.input.radio(
              ^.value := pet.status.toString,
              ^.checked := pet.status == Adopted,
              ^.onChange --> p.stateChange(pet.copy(status = Adopted))
            ),
            "Adopted"
          ),
          <.label(
            <.input.radio(
              ^.value := pet.status.toString,
              ^.checked := pet.status == Pending,
              ^.onChange --> p.stateChange(pet.copy(status = Pending))
            ),
            "Pending"
          ),
          <.label(
            <.input.radio(
              ^.value := pet.status.toString,
              ^.checked := pet.status == Available,
              ^.onChange --> p.stateChange(pet.copy(status = Available))
            ),
            "Available"
          ),
          <.span(" "),
          <.span("TYPE: " + pet.category),<.span(" "),
          <.span("BIO: " + pet.bio),<.span(" "),
          //<.span("ID: " + pet.id),<.span(" "),
          pet.status match {
            case Adopted | Pending => <.s(pet.status.toString)
            case Available => <.span(pet.status.toString)
          },
          <.div(bss.floatRight, Button(onClick = p.editItem(pet).toJsCallback)("Edit")),
          <.div(bss.floatRight, Button(onClick = p.deleteItem(pet).toJsCallback)("Delete"))
        )
      }
      <.ul(style.listGroup)(p.pets toTagMod renderItem)
    })
    .build

  def apply(items: Seq[Pet], stateChange: Pet => Callback, editItem: Pet => Callback, deleteItem: Pet => Callback) =
    PetList(PetListProps(items, stateChange, editItem, deleteItem))
}
