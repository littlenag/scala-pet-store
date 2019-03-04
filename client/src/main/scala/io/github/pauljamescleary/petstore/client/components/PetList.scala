package io.github.pauljamescleary.petstore.client.components

import io.github.pauljamescleary.petstore.client.bootstrap.{Badge, Button, Table}
import io.github.pauljamescleary.petstore.client.css.GlobalStyles
import io.github.pauljamescleary.petstore.client.img.FontAwesomeTags
import io.github.pauljamescleary.petstore.domain.pets.Pet
import io.github.pauljamescleary.petstore.domain.pets.PetStatus.{Adopted, Available, Pending}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object PetList {
  // shorthand for styles
  case class PetListProps(
                           pets: Seq[Pet],
                           stateChange: Pet => Callback,
                           editItem: Pet => Callback,
                           deleteItem: Pet => Callback
                          )

  private val PetList = ScalaComponent.builder[PetListProps]("PetList")
    .render_P(p => {
      val head = <.thead(
        <.tr(
          <.th("#"),
          <.th("Status"),
          <.th("Name"),
          <.th("Category"),
          <.th("Bio"),
          <.th("Tags"),
          //<.th("Photos"),
          <.th("Actions")
        )
      )

      def renderPet(pet: Pet) = {
        val id = pet.id.getOrElse(-1l)
        <.tr(
          // # / id
          <.td(id),
          // status
          <.td(
            pet.status match {
              case Adopted | Pending => <.s(pet.status.toString)
              case Available => <.span(pet.status.toString)
            }
          ),
          <.td(pet.name),
          <.td(pet.category),
          <.td(pet.bio),
          <.td(pet.tags toTagMod {t => Badge(variant = "light")(t)}),
          //<.td(pet.photoUrls toTagMod {t => Badge(variant = "light")(t)}),
          <.td(
            GlobalStyles.bootstrapStyles.floatRight,
            Button(variant = "secondary", onClick = p.deleteItem(pet).toJsCallback)(FontAwesomeTags.trash, " Delete"),
            <.span(),
            Button(variant = "primary", onClick = p.editItem(pet).toJsCallback)(FontAwesomeTags.edit, " Edit")
          )
        )
      }

      val body = <.tbody(p.pets toTagMod renderPet)

      Table(striped = true, bordered = true, hover = true, size = "sm")(head,body)
    })
    .build

  def apply(items: Seq[Pet], stateChange: Pet => Callback, editItem: Pet => Callback, deleteItem: Pet => Callback) =
    PetList(PetListProps(items, stateChange, editItem, deleteItem))
}
