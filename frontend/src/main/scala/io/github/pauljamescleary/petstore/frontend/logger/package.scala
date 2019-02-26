package io.github.pauljamescleary.petstore.frontend

/**
  *
  */
package object logger {
  private val defaultLogger = LoggerFactory.getLogger("Log")

  def log = defaultLogger
}
