package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._

class Stepper[T](initial: T, event: Event[T]) extends Switcher[T](initial, event.map((t, v) => new Constant(v))) {}

object Stepper {
  def apply[T](initial: T, event: Event[T]) : Stepper[T] = {
    new Stepper(initial, event)
  }
}