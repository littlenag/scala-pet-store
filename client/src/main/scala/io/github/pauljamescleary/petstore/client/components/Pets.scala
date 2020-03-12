package io.github.pauljamescleary.petstore.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import io.github.pauljamescleary.petstore.domain
import io.github.pauljamescleary.petstore.client
import domain.pets.Pet
import domain.pets.PetStatus.{Adopted, Available, Pending}
import client.logger._
import client.services.{DeletePet, PetsData, RefreshPets, UpsertPet}
import typings.materialUiCore.components.{Button, Card, CardContent, CardHeader, TextField, Modal, Typography}
import io.github.pauljamescleary.petstore.client.img.FontAwesomeTags
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object Pets {

  case class Props(proxy: ModelProxy[Pot[PetsData]])

  case class State(selectedItem: Option[Pet] = None, showPetForm: Boolean = false)

  class Backend($: ScalaComponent.BackendScope[Props, State]) {
    def mounted(props: Props) =
    // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshPets))

    def editPet(item: Option[Pet]) =
    // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showPetForm = true))

    def petEdited(item: Pet, cancelled: Boolean) = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("Pet editing cancelled")
      } else {
        Callback.log(s"Pet edited: $item") >>
          $.props >>= (_.proxy.dispatchCB(UpsertPet(item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showPetForm = false))
    }

    def render(p: Props, s: State) =
      Card()(
        CardHeader()("Pets in the Store"),
        CardContent()(
          <.div(
            Button(onClick = _ => editPet(None))(
              FontAwesomeTags.plusSquare, " New"
            ),
            p.proxy().renderFailed(ex => "Error loading"),
            p.proxy().renderPending(_ > 500, _ => "Loading..."),
            p.proxy().render(pd =>
              PetList(
                pd.pets,
                item => p.proxy.dispatchCB(UpsertPet(item)),
                item => editPet(Some(item)),
                item => p.proxy.dispatchCB(DeletePet(item))
              )
            ),
            // if the dialog is open, add it to the panel
            if (s.showPetForm)
              PetForm(PetForm.Props(s.selectedItem, petEdited))
            else // otherwise add an empty placeholder
              VdomArray.empty()
          )
        )
      )
  }

  // create the React component for To Do management
  val component = ScalaComponent.builder[Props]("Pets")
    .initialState(State()) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[PetsData]]) = component(Props(proxy))
}

object PetForm {
  case class Props(item: Option[Pet], submitHandler: (Pet, Boolean) => Callback)

  case class State(pet: Pet, tagText: String, cancelled: Boolean = true, show: Boolean = true)

  class Backend($: ScalaComponent.BackendScope[Props, State]) {

    def cancel(): Callback = {
      $.modState(s => s.copy(cancelled = true, show = false))
    }

    def save(): Callback = {
      $.modState(s => s.copy(cancelled = false, show = false))
    }

    def onExit(state: State, props: Props): Callback = {
      props.submitHandler(state.pet.copy(tags = state.tagText.split(",").map(_.trim).toSet), state.cancelled)
    }

    def updateBio(e: ReactEventFromInput) = {
      val text = e.target.value
      $.modState(s => s.copy(pet = s.pet.copy(bio = text)))
    }

    def updateName(e: ReactEventFromInput) = {
      val text = e.target.value
      $.modState(s => s.copy(pet = s.pet.copy(name = text)))
    }

    def updateCategory(e: ReactEventFromInput) = {
      val text = e.target.value
      $.modState(s => s.copy(pet = s.pet.copy(category = text)))
    }

    def updateTags(e: ReactEventFromInput) = {
      val text = e.target.value
      $.modState(s => s.copy(tagText = text))
    }

    def updateStatus(e: ReactEventFromInput) = {
      // update TodoItem priority
      val newStatus = e.currentTarget.value match {
        case p if p == Available.toString => Available
        case p if p == Pending.toString => Pending
        case p if p == Adopted.toString => Adopted
      }
      $.modState(s => s.copy(pet = s.pet.copy(status = newStatus)))
    }

    def render(p: Props, s: State) = {
      log.debug(s"User is ${if (s.pet.id.isEmpty) "adding" else "editing"} a pet or two")
      val headerText = if (s.pet.id.isEmpty) "Add new pet" else "Edit pet"

      Modal(
        open = s.show,
        onClose = _ => onExit(s,p), // to handle clicking outside the modal
      )(
        <.h2(
          headerText
        ),

        /**
          * <.th("#"),
          * <.th("Status"),
          * <.th("Name"),
          * <.th("Category"),
          * <.th("Bio"),
          * <.th("Tags"),
          * //<.th("Photos"),
          * <.th("Actions")
          */
        <.div(
          <.div(
            <.label(^.`for` := "status", "Status"),
            // using defaultValue = "Normal" instead of option/selected due to React
            <.select(^.id := "status", ^.value := s.pet.status.toString, ^.onChange ==> updateStatus,
              <.option(^.value := Available.toString, "Available"),
              <.option(^.value := Pending.toString, "Pending"),
              <.option(^.value := Adopted.toString, "Adopted")
            )
          ),
          <.div(
            <.label(^.`for` := "name", "Name"),
            <.input.text(^.id := "name", ^.value := s.pet.name,
              ^.placeholder := "Name", ^.onChange ==> updateName)
          ),
          <.div(
            <.label(^.`for` := "category", "Category"),
            <.input.text(^.id := "category", ^.value := s.pet.category,
              ^.placeholder := "Category", ^.onChange ==> updateCategory)
          ),
          <.div(
            <.label(^.`for` := "bio", "Biography"),
            <.input.text(^.id := "bio", ^.value := s.pet.bio,
              ^.placeholder := "Biography", ^.onChange ==> updateBio)
          ),
          <.div(
            <.label(^.`for` := "tags", "Tags"),
            <.input.text(^.id := "tags", ^.value := s.tagText,
              ^.placeholder := "Tags (comma separated)", ^.onChange ==> updateTags)
          )
        ),
        <.div(
          Button(onClick = _ => cancel())("Close"),
          Button(onClick = _ => save())("Save Changes")
        )
      )
    }
  }

  val component = ScalaComponent.builder[Props]("PetForm")
    .initialStateFromProps { p =>
      val pet = p.item.getOrElse(Pet("", "", ""))
      State(pet, pet.tags.mkString(", "))
    }
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}
