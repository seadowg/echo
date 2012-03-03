package com.github.oetzi.echo.types

import com.github.oetzi.echo.core._
import com.github.oetzi.echo.Echo._

class Stepper[T](initial: T, val event: EventSource[T]) extends Behaviour[T](
  Stepper.construct(initial, event)) {}

object Stepper {
  private def construct[T](initial: T, event: EventSource[T]): Time => T = {
    {
      time =>
        val occ = event.top(time)

        if (occ == None) {
          initial
        }
        
        else {
          occ.get.value
        }
    }
  }
}