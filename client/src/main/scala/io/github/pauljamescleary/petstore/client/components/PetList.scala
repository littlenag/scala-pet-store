package io.github.pauljamescleary.petstore.client.components

import typings.materialUiCore.components.{Button, Chip, Table, TableBody, TableCell, TableHead, TableRow}
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
      val head =
        TableHead()(
          TableRow()(
            TableCell()("#"),
            TableCell()("Status"),
            TableCell()("Name"),
            TableCell()("Category"),
            TableCell()("Bio"),
            TableCell()("Tags"),
            //<.th("Photos"),
            TableCell()("Actions")
          )
        )

      def renderPet(pet: Pet): VdomNode = {
        val id = pet.id.getOrElse(-1l)
        TableRow()(
          // # / id
          TableCell()(id),
          // status
          TableCell()(
            pet.status match {
              case Adopted | Pending => <.s(pet.status.toString)
              case Available => <.span(pet.status.toString)
            }
          ),
          TableCell()(pet.name),
          TableCell()(pet.category),
          TableCell()(pet.bio),
          TableCell()(pet.tags.map{t => Chip()(t):VdomNode}.toSeq: _*),
          //<.td(pet.photoUrls toTagMod {t => Badge(variant = "light")(t)}),
          TableCell()(
            Button(onClick = _ => p.deleteItem(pet))(FontAwesomeTags.trash, " Delete"),
            <.span(),
            Button(onClick = _ => p.editItem(pet))(FontAwesomeTags.edit, " Edit")
          )
        )
      }

      val body = TableBody()(p.pets.map(renderPet):_*)

      Table()(head,body)
    })
    .build

  def apply(items: Seq[Pet], stateChange: Pet => Callback, editItem: Pet => Callback, deleteItem: Pet => Callback) =
    PetList(PetListProps(items, stateChange, editItem, deleteItem))
}
