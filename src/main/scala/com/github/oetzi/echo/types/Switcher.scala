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
        event.lastValueAt(time).getOrElse(initial).at(time)
    }
  }
}

}