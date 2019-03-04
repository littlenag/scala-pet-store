package io.github.pauljamescleary.petstore.client

import scala.scalajs.js

/**
  *
  */
package object logger {
  private val defaultLogger = LoggerFactory.getLogger("Log")

  def log = defaultLogger

  /**
    * Adds an extension method to insert the log4javascript object in global scope. Useful for debugging.
    */
  implicit class LoggingExtensions(l4j: log4javascript.type) {
    def addToGlobalScope() = {
      js.Dynamic.global.log4javascript = log4javascript
    }
  }
}
