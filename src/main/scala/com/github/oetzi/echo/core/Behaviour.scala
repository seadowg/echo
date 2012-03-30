package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._

/** `Behaviour` provides an implementation of FRP Behaviours.
 */

class Behaviour[T](private val rule: Time => T) {
  var last: (Time, T) = null
  
  def eval(): T = {
    groupLock synchronized {
      this.at(now())
    }
  }

  protected[echo] def at(time: Time): T = {
    if (last == null || time != last._1) {
      last = (time, rule(time))
    }

    last._2
  }

  def sample[A](sourceEvent: Event[A]): Event[T] = {
    frp {
      val source = new EventSource[T] {
        sourceEvent.hook {
          occ => occur(Behaviour.this.at(occ.time))
        }
      }

      source.event()
    }
  }

  def until[A](event: Event[A], behaviour: Behaviour[T]): Behaviour[T] = {
    frp {
      val rule: Time => T = {
        time =>
          val occ = event.top()

          if (occ == None) {
            this.at(time)
          }

          else {
            behaviour.at(time)
          }
      }

      new Behaviour(rule)
    }
  }

  def until[A](after: Time, event: Event[A], behaviour: Behaviour[T]): Behaviour[T] = {
    frp {
      val rule: Time => T = {
        time =>
          val occ = event.top()

          if (occ == None || occ.get.time < after) {
            this.at(time)
          }

          else {
            behaviour.at(time)
          }
      }

      new Behaviour(rule)
    }
  }

  def toggle[A](event: Event[A], behaviour: Behaviour[T]): Behaviour[T] = {
    frp {
      val rule: Time => T = {
        time =>
          val occ = event.top()

          if (occ == None || occ.get.num % 2 == 0) {
            this.at(time)
          }

          else {
            behaviour.at(time)
          }
      }

      new Behaviour(rule)
    }
  }

  def map[B](func: T => B): Behaviour[B] = {
    frp {
      new Behaviour(time => func(this.at(time)))
    }
  }

  def map2[U, V](behaviour: Behaviour[U])(func: (T, U) => V): Behaviour[V] = {
    frp {
      new Behaviour(time => func(this.at(time), behaviour.at(time)))
    }
  }

  def map3[U, V, W](beh1: Behaviour[U], beh2: Behaviour[V])(func: (T, U, V) => W): Behaviour[W] = {
    frp {
      new Behaviour(time => func(this.at(time), beh1.at(time), beh2.at(time)))
    }
  }
}

class Constant[T](val value: T) extends Behaviour[T](time => value) {
  override def eval(): T = {
    value
  }

  override protected[echo] def at(time: Time): T = {
    value
  }
}

object Behaviour {
  def apply[T](rule: Time => T): Behaviour[T] = {
    new Behaviour(rule)
  }
}