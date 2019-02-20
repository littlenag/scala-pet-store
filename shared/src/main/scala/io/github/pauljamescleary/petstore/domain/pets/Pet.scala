package io.github.pauljamescleary.petstore.domain.pets

import io.github.pauljamescleary.petstore.shared.JsonSerializers._

case class Pet(
    name: String,
    category: String,
    bio: String,
    status: PetStatus = PetStatus.Available,
    tags: Set[String] = Set.empty,
    photoUrls: Set[String] = Set.empty,
    id: Option[Long] = None
)

object Pet {
  implicit val decodePet = deriveDecoder[Pet]
  implicit val encodePet = deriveEncoder[Pet]
}