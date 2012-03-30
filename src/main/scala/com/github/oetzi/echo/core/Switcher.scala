package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._

class Switcher[T](behaviour: Behaviour[T], val event: Event[Behaviour[T]]) extends Behaviour[T](
  Switcher.construct(behaviour, event)) {
}

object Switcher {
  def apply[T](initial: Behaviour[T], event: Event[Behaviour[T]]) : Switcher[T] = {
    new Switcher(initial, event)
  }
  
  private def construct[T](initial: Behaviour[T], event: Event[Behaviour[T]]): Time => T = {
    frp {
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
}