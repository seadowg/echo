import com.github.oetzi.echo.core._
import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.types {

class Switcher[T](behaviour: Behaviour[T], val event: EventSource[Behaviour[T]]) extends Behaviour[T](
  Switcher.construct(behaviour, event)) {
}

object Switcher {
  private def construct[T](initial: Behaviour[T], event: EventSource[Behaviour[T]]): Time => T = {
    {
      time =>
        val filter = event.occs().filter(occ => occ.time <= time)

        if (filter.length > 0) {
          filter.last.value.at(time)
        }

        else {
          initial.at(time)
        }
    }
  }
}

}