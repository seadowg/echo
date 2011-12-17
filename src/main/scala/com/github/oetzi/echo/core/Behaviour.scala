import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {

class Behaviour[T](private val rule: Time => T) {
  def at(time: Time): T = {
    rule(time)
  }

  def sample[A](event: EventSource[A]): EventSource[T] = {
    event.map {
      occurrence =>
        new Occurrence(occurence.time, this.at(occurence.time))
    }
  }

  def until[A](event: EventSource[A], newRule: Time => T): Behaviour[T] = {
    val rule: Time => T = {
      time =>
        if (!event.occs().isEmpty && event.occs().head.time <= time) {
          newRule(time)
        }
        else {
          this.rule(time)
        }
    }

    new Behaviour(rule)
  }

  def map[B](func: T => B): Behaviour[B] = {
    new Behaviour(time => func(this.at(time)))
  }
}

}