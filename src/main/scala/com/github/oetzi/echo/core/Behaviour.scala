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
  
  def toggle[A](event : EventSource[A], behaviour : Behaviour[T]) : Behaviour[T] = {
    val rule: Time => T = {
      time =>
        if (event.occs().filter(occ => occ.time <= time).length % 2 == 0) {
          this.rule(time)
        }

        else {
          behaviour.rule(time)
        }
    }

    new Behaviour(rule)
  }

  def map[B](func: T => B): Behaviour[B] = {
    new Behaviour(time => func(this.at(time)))
  }

  def map1[U, V](behaviour: Behaviour[U])(func: (T, U) => V): Behaviour[V] = {
    new Behaviour(time => func(this.at(time), behaviour.at(time)))
  }

  def map2[U, V, W](beh1: Behaviour[U], beh2: Behaviour[V])(func: (T, U, V) => W): Behaviour[W] = {
    new Behaviour(time => func(this.at(time), beh1.at(time), beh2.at(time)))
  }
}

object Behaviour {
  def apply[T](rule: Time => T): Behaviour[T] = {
    new Behaviour(rule)
  }
}

}