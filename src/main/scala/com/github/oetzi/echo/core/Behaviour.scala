import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {

class Behaviour[T](private val rule: Time => T) {
  def at(time: Time): T = {
    rule(time)
  }

  def transform(func: Time => Time): Behaviour[T] = {
    new Behaviour(time => this.at(func(time)))
  }

  def sample[A](event: EventSource[A]): Event[T] = {
    event.map {
      occurrence =>
        new Occurrence(occurrence.time, this.at(occurrence.time))
    }
  }

  def until[A](event: EventSource[A], behaviour: Behaviour[T]): Behaviour[T] = {
    val rule: Time => T = {
      time =>
        if (!event.occs().isEmpty && event.occs().head.time <= time) {
          behaviour.at(time)
        }
        else {
          this.at(time)
        }
    }

    new Behaviour(rule)
  }

  def until[A](time: Time, event: EventSource[A], behaviour: Behaviour[T]): Behaviour[T] = {
    this.until(event.filter(occ => occ.time >= time), behaviour)
  }

  def map[B](func: T => B): Behaviour[B] = {
    new Behaviour(time => func(this.at(time)))
  }
}

object Behaviour {
  def apply[T](rule: Time => T): Behaviour[T] = {
    new Behaviour(rule)
  }
}

}