package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.EchoApp
import collection.mutable.ArrayBuffer

/**Event provides an implementation of FRP Events.
 */
trait Event[T] {
  /**Returns the last occurrence for this Event
   */
  protected def occs(): Occurrence[T]

  /**Causes the passed function to execute every time this
   * Event occurs with the currently occurring occurrence
   * as input.
   */
  protected[echo] def hook(block: Occurrence[T] => Unit)

  /**Returns a new Event that's occurrence values
   * are are the result of of transforming the
   * callee's occurrences with the given function (occurrence
   * times are unmodified).
   */
  def map[U](func: (Time, T) => U): Event[U] = {
    frp {
      val mapFun: () => Occurrence[U] = {
        () =>
          val occ = occs()

          if (occ == null) {
            null
          }

          else {
            occ.map(func)
          }
      }

      val source = this

      new Event[U] {
        protected def occs(): Occurrence[U] = {
          mapFun()
        }

        protected[echo] def hook(block: Occurrence[U] => Unit) {
          val mapBlock: Occurrence[T] => Unit = {
            occ => block(occ.map(func))
          }

          source.hook(mapBlock)
        }
      }
    }
  }

  /**Returns an Event that filters occurrences
   * occurrences (via the value) of the callee
   * with the given predicate.
   */
  def filter(func: T => Boolean): Event[T] = {
    frp {
      val source = this

      new EventSource[T] {
        source.hook {
          occ =>
            if (func(occ.value)) {
              occur(occ.value)
            }
        }
      }
    }
  }

  /**Returns an Event that's occurrences
   * are a merged version of the callee
   * and the given Event (merged in time order
   * with left precedence).
   */
  def merge(event: Event[T]): Event[T] = {
    frp {
      val func: () => Occurrence[T] = {
        () =>
          val left = this.occs()
          val right = event.top().getOrElse(null)

          if (left == null && right == null) {
            null
          }

          else if (left == null) {
            right
          }

          else if (right == null) {
            left
          }

          else if (left.time <= right.time) {
            new Occurrence(right.time, right.value, left.num + right.num)
          }

          else {
            new Occurrence(left.time, left.value, left.num + right.num)
          }
      }

      val source = this

      new Event[T] {
        protected def occs(): Occurrence[T] = {
          func()
        }

        protected[echo] def hook(block: Occurrence[T] => Unit) {
          source.hook(block)
          event.hook(block)
        }
      }
    }
  }

  /**Returns the result of calling occs wrapped
   * in an Option[Occurrence[T]] instance.
   */
  private[core] def top(): Option[Occurrence[T]] = {
    val top = occs()

    if (top != null) {
      Some(top)
    }

    else {
      None
    }
  }
}

/**EventSource allows for some external input or output to
 * be represented as an Event.
 */
trait EventSource[T] extends Event[T] {
  private val hooks: ArrayBuffer[Occurrence[T] => Unit] = new ArrayBuffer()
  private var present: Occurrence[T] = null
  private var length: BigInt = 0

  /**Returns a simple Event view of this EventSource.
   */
  def event(): Event[T] = {
    val source = this

    new Event[T] {
      protected def occs(): Occurrence[T] = {
        source.occs()
      }

      protected[echo] def hook(block: Occurrence[T] => Unit) {
        source.hook(block)
      }
    }
  }

  protected def occs(): Occurrence[T] = {
    present
  }

  /**Causes the EventSource to occur with the
   * given value at the time of the call. This function
   * is atomic with respect to the run-time
   * group of FRP objects so occurrence times
   * are guaranteed to be monotonically increasing for
   * each Event (even if multiple threads can access the occur
   * function)
   */
  protected def occur(value: T) {
    while (!createLock.available) {}

    groupLock synchronized {
      length += 1
      val occ = new Occurrence(now(), value, length)
      present = occ

      echo(occ)
    }
  }

  protected[echo] def hook(block: Occurrence[T] => Unit) {
    hooks += block
  }

  protected def echo(occurrence: Occurrence[T]) {
    hooks.foreach {
      block => block(occurrence)
    }
  }
}

protected class Occurrence[T](val time: Time, val value: T, val num: BigInt) {
  def map[U](func: (Time, T) => U): Occurrence[U] = {
    new Occurrence(time, func(time, value), num)
  }
}

object Event {

  /**Returns an Event that occurs once at time 0.
   */
  def apply[T](value: T): Event[T] = {
    frp {
      new EventSource[T] {
        EchoApp.afterSetup {
          () => occur(value)
        }
      }.event()
    }
  }

  /**Returns an Event that never occurs.
   */
  def apply[T](): Event[T] = {
    frp {
      new EventSource[T] {}.event()
    }
  }

  /**Flattens an Event[Event[T]] to an Event[T]. Occurrences
   * of the inner Events will be `delayed` to the time
   * that their parent occurs if they occur any earlier. Apart
   * from this caveat it is essentially a merge of all
   * Events in the outer Event.
   */
  def join[T](eventEvent: Event[Event[T]]): Event[T] = {
    frp {
      new EventSource[T] {
        private var priorityCount: BigInt = 0
        private var lastPriority: BigInt = 0

        eventEvent.hook {
          e =>
            val priority = priorityCount
            priorityCount += 1

            e.value.hook {
              occ => joinOccur(occ, priority)
            }

            val old = e.value.top()
            if (old != None) joinOccur(old.get, priority)
        }

        private def joinOccur(occurrence: Occurrence[T], priority: BigInt) {
          this synchronized {
            val lastOcc = this.top()

            if (lastOcc != None && occurrence.time == lastOcc.get.time) {
              if (priority >= lastPriority) {
                occur(occurrence.value)
                lastPriority = priority
              }

              else {
                echo(occurrence)
              }
            }

            else {
              occur(occurrence.value)
              lastPriority = priority
            }
          }
        }
      }.event()
    }
  }
}