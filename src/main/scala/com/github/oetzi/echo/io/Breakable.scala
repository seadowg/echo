package com.github.oetzi.echo.io

import com.github.oetzi.echo.core.{Occurrence, Event}
import com.github.oetzi.echo.Echo._


trait Breakable {
  val errors: Event[Exception] = Event[Exception]()

  protected def dangerous[T](block: () => T): Option[T] = {
    try {
      Some(block())
    }

    catch {
      case e: Exception => {
        this.errors.occur(new Occurrence(now, e))
        None
      }
    }
  }
}