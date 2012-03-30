package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._

/** Behaviour provides an implementation of FRP Behaviours.
 */
sealed class Behaviour[T](private val rule: Time => T) {
  var last: (Time, T) = null
  
  /** Evaluates the Behaviour at the current time. The function is atomic
    * with respect to the run-time group of FRP objects so evaluation times
    * are guaranteed to be monotonically increasing (even for Behaviours part
    * of more than one composite Behaviour and for concurrently evaluated 
    * Behaviours.)
   */
  def eval(): T = {
    groupLock synchronized {
      this.at(now())
    }
  }

  /** Returns a Event[T] that occurs every time sourceEvent occurs
    * with the value of the Behaviour at that time.
   */
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

  /** Returns a Behaviour that behaves as the callee until the
    * Event occurs. It then switches to behaving as the passed
    * Behaviour.
    */
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

  /** Similar to the previous until funtion except for the Event must
    * have occurred on or after the specified time for the Behaviour to
    * switch.
   */
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

  /** Returns a Behaviour that toggles between behaving as the callee
    * and the passed Behaviour whenever the passed Event occurs.
   */
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

  /** Returns a Behaviour that transorms the callee's
    * value with the passed function.
   */
  def map[B](func: T => B): Behaviour[B] = {
    frp {
      new Behaviour(time => func(this.at(time)))
    }
  }
  
  /** Returns a Behaviour that transorms the callee's
    * and passed Behavior's value with the passed function.
   */
  def map2[U, V](behaviour: Behaviour[U])(func: (T, U) => V): Behaviour[V] = {
    frp {
      new Behaviour(time => func(this.at(time), behaviour.at(time)))
    }
  }

  /** Returns a Behaviour that transorms the callee's
    * and passed Behaviors' value with the passed function.
   */
  def map3[U, V, W](beh1: Behaviour[U], beh2: Behaviour[V])(func: (T, U, V) => W): Behaviour[W] = {
    frp {
      new Behaviour(time => func(this.at(time), beh1.at(time), beh2.at(time)))
    }
  }
  
  private[core] def at(time: Time): T = {
    if (last == null || time != last._1) {
      last = (time, rule(time))
    }

    last._2
  }
}

object Behaviour {
  def apply[T](rule: Time => T): Behaviour[T] = {
    new Behaviour(rule)
  }
}

/** Switcher represents a Behaviour that's value is always the latest evaluated occurrence in
  * a given Event[Behaviour].
 */
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
        val occ = event.top()

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

/** Stepper is a a static valued version of Switcher that represents the lastest
  * occurrence in an Event[T].
 */
class Stepper[T](initial: T, event: Event[T]) extends Switcher[T](initial, event.map((t, v) => new Constant(v))) {}

object Stepper {
  def apply[T](initial: T, event: Event[T]) : Stepper[T] = {
    new Stepper(initial, event)
  }
}

/** Constant is a Behaviour that's value never changes. It is omptimised so
  * so it only returns the value rather than evaluating it needlessly with respect
  * to time.
 */
protected[echo] class Constant[T](val value: T) extends Behaviour[T](time => value) {
  override def eval(): T = {
    value
  }

  override private[core] def at(time: Time): T = {
    value
  }
}