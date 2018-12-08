package io.github.pauljamescleary.petstore.shared

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
trait PetstoreApi {
  // message of the day
  def welcomeMsg(name: String): String
}
