package io.github.pauljamescleary.petstore

import io.circe.Decoder
import io.circe.generic.semiauto._

package object config {
  implicit val mlDec: Decoder[MailerConfig] = deriveDecoder
  implicit val srDec: Decoder[ServerConfig] = deriveDecoder
  implicit val dbDec: Decoder[DatabaseConfig] = deriveDecoder
  implicit val psDec: Decoder[PetStoreConfig] = deriveDecoder
}
