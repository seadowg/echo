package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.EchoApp
import collection.mutable.ArrayBuffer

/** `Event` provides an implementation of FRP Events.
 */

trait Event[T] {
  protected def occs(): Occurrence[T]
  protected[echo] def hook(block: Occurrence[T] => Unit)

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

  // echo utility functions
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

/** `EventSource` allows FRP Events to hook into external frameworks
  * and sources. 
 */

trait EventSource[T] extends Event[T] {
  private val hooks: ArrayBuffer[Occurrence[T] => Unit] = new ArrayBuffer()
  private var present: Occurrence[T] = null
  private var length: BigInt = 0

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

  protected def occur(value: T) {
    while (!createLock.available) {}

    groupLock synchronized {
      length += 1
      val occ = new Occurrence(now(), value, length)
      present = occ

      echo(occ)
    }
  }

  protected def echo(occurrence: Occurrence[T]) {
    hooks.foreach {
      block => block(occurrence)
    }
  }

  protected[echo] def hook(block: Occurrence[T] => Unit) {
    hooks += block
  }
}

protected class Occurrence[T](val time: Time, val value: T, val num: BigInt) {
  def map[U](func: (Time, T) => U): Occurrence[U] = {
    new Occurrence(time, func(time, value), num)
  }
}

object Event {
  def apply[T](value: T): Event[T] = {
    frp {
      new EventSource[T] {
        EchoApp.afterSetup {
          () => occur(value)
        }
      }.event()
    }
  }

  def apply[T](): Event[T] = {
    frp {
      new EventSource[T] {}.event()
    }
  }

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