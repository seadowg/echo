package com.github.oetzi.echo.io

import com.github.oetzi.echo.core.Event

trait Breakable {
  val errors: Event[Exception] = Event[Exception]
}