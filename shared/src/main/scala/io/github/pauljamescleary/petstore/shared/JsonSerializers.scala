package io.github.pauljamescleary.petstore.shared

import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}

import io.circe.Json
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.decoding.ConfiguredDecoder
import io.circe.generic.extras.encoding.ConfiguredObjectEncoder
import shapeless.Lazy

import scala.language.implicitConversions

object JsonSerializers {
  import io.circe.{Decoder, Encoder}

  implicit def encodeZonedDateTime: Encoder[ZonedDateTime] = io.circe.java8.time.encodeZonedDateTime
  implicit def encodeLocalDateTime: Encoder[LocalDateTime] = io.circe.java8.time.encodeLocalDateTime
  implicit def encodeLocalDate: Encoder[LocalDate] = io.circe.java8.time.encodeLocalDate
  implicit def encodeLocalTime: Encoder[LocalTime] = io.circe.java8.time.encodeLocalTime

  implicit def decodeZonedDateTime: Decoder[ZonedDateTime] = io.circe.java8.time.decodeZonedDateTime
  implicit def decodeLocalDateTime: Decoder[LocalDateTime] = io.circe.java8.time.decodeLocalDateTime
  implicit def decodeLocalDate: Decoder[LocalDate] = io.circe.java8.time.decodeLocalDate
  implicit def decodeLocalTime: Decoder[LocalTime] = io.circe.java8.time.decodeLocalTime

  implicit val circeConfiguration: Configuration = Configuration.default.withDefaults

  def deriveDecoder[A](implicit decode: Lazy[ConfiguredDecoder[A]]) = io.circe.generic.extras.semiauto.deriveDecoder[A]
  def deriveEncoder[A](implicit decode: Lazy[ConfiguredObjectEncoder[A]]) = io.circe.generic.extras.semiauto.deriveEncoder[A]
  def deriveFor[A](implicit decode: Lazy[ConfiguredDecoder[A]]) = io.circe.generic.extras.semiauto.deriveFor[A]

  //implicit def encoderOps[A](a: A): io.circe.syntax.EncoderOps[A] = io.circe.syntax.EncoderOps[A](a)
  def parseJson(s: String) = io.circe.parser.parse(s)
  def decodeJson[A](s: String)(implicit decoder: Decoder[A]) = io.circe.parser.decode[A](s)

  def extract[T: Decoder](json: Json) = json.as[T] match {
    case Right(u) => u
    case Left(x) => throw x
  }
}
