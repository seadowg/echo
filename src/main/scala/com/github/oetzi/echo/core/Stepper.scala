package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._

class Stepper[T](initial: T, val event: Event[T]) extends Behavior[T](Stepper.construct(initial, event)) {}

object Stepper {
  private def construct[T](initial: T, event: Event[T]): Time => T = {
    frp {
      () => {
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
}