package com.github.oetzi.echo.io

import com.github.oetzi.echo.core.EventSource


trait Breakable {
  val errors = new EventSource[Exception] {
    def apply[T](block: () => T): Option[T] = {
      try {
        Some(block())
      }

      catch {
        case e: Exception => {
          this.occur(e)
          None
        }
      }
    }
  }

  protected def dangerous[T](block: () => T): Option[T] = {
    errors(block)
  }
}