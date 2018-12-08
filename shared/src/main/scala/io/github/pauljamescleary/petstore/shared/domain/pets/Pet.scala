package io.github.pauljamescleary.petstore.shared.domain.pets

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
case class Pet(
    name: String,
    category: String,
    bio: String,
    status: PetStatus = PetStatus.Available,
    tags: Set[String] = Set.empty,
    photoUrls: Set[String] = Set.empty,
    id: Option[Long] = None
)
