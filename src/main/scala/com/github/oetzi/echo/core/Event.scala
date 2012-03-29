package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import collection.mutable.ArrayBuffer
import collection.mutable.Queue

trait Event[T] {
  protected def occs(time: Time): Occurrence[T]
  protected[echo] def hook(block: Occurrence[T] => Unit)

  def map[U](func: (Time, T) => U): Event[U] = {
    frp {
      val mapFun: Time => Occurrence[U] = {
        time =>
          val occ = occs(time)

          if (occ == null) {
            null
          }

          else {
            occ.map(func)
          }
      }

      val source = this

      new Event[U] {
        protected def occs(time: Time): Occurrence[U] = {
          mapFun(time)
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

	def filter(func: T => Boolean) : Event[T] = {
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
      val func: Time => Occurrence[T] = {
        time =>
          val left = this.occs(time)
          val right = event.top(time).getOrElse(null)

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
        protected def occs(time: Time): Occurrence[T] = {
          func(time)
        }

        protected[echo] def hook(block: Occurrence[T] => Unit) {
          source.hook(block)
          event.hook(block)
        }
      }
    }
  }

  // echo utility functions
  def top(time: Time): Option[Occurrence[T]] = {
    val top = occs(time)

    if (top != null) {
      Some(top)
    }

    else {
      None
    }
  }
}

trait EventSource[T] extends Event[T] {
  private val hooks: ArrayBuffer[Occurrence[T] => Unit] = new ArrayBuffer()
  private var present: Occurrence[T] = null
  private var length: BigInt = 0

  def event(): Event[T] = {
    val source = this

    new Event[T] {
      protected def occs(time: Time): Occurrence[T] = {
        source.occs(time)
      }

      protected[echo] def hook(block: Occurrence[T] => Unit) {
        source.hook(block)
      }
    }
  }

  protected def occs(time: Time): Occurrence[T] = {
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
  def apply[T](value: T) : Event[T] = {
    frp {
      new EventSource[T] {
        occur(value)
      }.event
    }
  }
  
  def apply[T]() : Event[T] = {
    frp {
      new EventSource[T] {}.event
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

            val old = e.value.top(now())
            if (old != None) joinOccur(old.get, priority)
        }

        private def joinOccur(occurrence: Occurrence[T], priority: BigInt) {
          this synchronized {
            val lastOcc = this.top(now())

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