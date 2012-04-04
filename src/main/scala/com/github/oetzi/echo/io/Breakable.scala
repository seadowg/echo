package com.github.oetzi.echo.io

import com.github.oetzi.echo.core.EventSource

/** Trait for allowing failure tolerant FRP
  * components.
 */
trait Breakable {
  /** An Event that represents Exceptions
    * thrown while executing code within this
    * Breakable object.
   */
  val errors = new EventSource[Exception] {
    def apply[T](block: => T): Option[T] = {
      try {
        Some(block)
      }

      catch {
        case e: Exception => {
          this.occur(e)
          None
        }
      }
    }
  }

  /** All code that can cause an exception
    * should be passed to this funtion in a Breakable
    * implementation. Any Exceptions thrown will occur
    * as part of the errors Event.
   */
  protected def dangerous[T](block: => T): Option[T] = {
    errors(block)
  }
}