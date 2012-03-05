package com.github.oetzi.echo.types

import com.github.oetzi.echo.core._
import com.github.oetzi.echo.Echo._

class Switcher[T](behaviour: Behaviour[T], val event: Event[Behaviour[T]]) extends Behaviour[T](
  Switcher.construct(behaviour, event)) {
}

object Switcher {
  private def construct[T](initial: Behaviour[T], event: Event[Behaviour[T]]): Time => T = {
    {
      time =>
        val occ = event.top(time)

        if (occ == None) {
          initial.at(time)
        }

        else {
          occ.get.value.at(time)
        }
    }
  }
}