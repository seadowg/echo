package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import collection.mutable.ArrayBuffer
import collection.mutable.Queue

trait Event[T] {
  protected def occs(time: Time): Occurrence[T]
  protected[echo] def hook(block: Occurrence[T] => Unit)

	def times() : Event[Time] = {
		frp {
			() =>
				val timeFun: Time => Occurrence[Time] = {
					time =>
						val occ = occs(time)
						
						if (occ == null) {
							null
						}
						
						else {
							occ.timePair
						}
				}
				
				val jsThisTrick = this
				
				new Event[Time] {
					protected def occs(time: Time): Occurrence[Time] = {
						timeFun(time)
					}
					
					protected[echo] def hook(block: Occurrence[Time] => Unit) {
						val timeBlock: Occurrence[T] => Unit = {
              occ => block(occ.timePair)
            }

            jsThisTrick.hook(timeBlock)
					}
				}
		}
	}

  def map[U](func: T => U): Event[U] = {
    frp {
      () =>
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

        val jsThisTrick = this

        new Event[U] {
          protected def occs(time: Time): Occurrence[U] = {
            mapFun(time)
          }

          protected[echo] def hook(block: Occurrence[U] => Unit) {
            val mapBlock: Occurrence[T] => Unit = {
              occ => block(occ.map(func))
            }

            jsThisTrick.hook(mapBlock)
          }
        }
    }
  }

	def filter(func: T => Boolean) : Event[T] = {
		frp {
			() =>
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
      () =>
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

        val jsThisTrick = this

        new Event[T] {
          protected def occs(time: Time): Occurrence[T] = {
            func(time)
          }

          protected[echo] def hook(block: Occurrence[T] => Unit) {
            jsThisTrick.hook(block)
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
  private val future : Queue[Occurrence[T]] = new Queue[Occurrence[T]]()
  private var present: Occurrence[T] = null
  private var length: BigInt = 0

  def event(): Event[T] = {
    val jsThisTrick = this

    new Event[T] {
      protected def occs(time: Time): Occurrence[T] = {
        jsThisTrick.occs(time)
      }

      protected[echo] def hook(block: Occurrence[T] => Unit) {
        jsThisTrick.hook(block)
      }
    }
  }

  protected def occs(time: Time): Occurrence[T] = {
    this synchronized {
			if (!future.isEmpty) {
				var head = future.headOption

		  	while (head != None && future.head.time <= time) {
	      	present = future.dequeue()
	      	head = future.headOption
	      }  	
		  }
		
			present
    }
  }

  protected def occur(value: T) {
    this synchronized {
      writeLock synchronized {
        while (!createLock.available) {}

        freezeTime(now()) {
          () =>
          	length += 1
						val occ = new Occurrence(now(), value, length)
            future += occ

            echo(occ)
        }
      }
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

class Occurrence[T](val time: Time, val value: T, val num: BigInt) {
  def map[U](func: T => U): Occurrence[U] = {
    new Occurrence(time, func(value), num)
  }

	def timePair() : Occurrence[Time] = {
		new Occurrence(time, time, num)
	}
}

object Event {
  def join[T](eventEvent: Event[Event[T]]): Event[T] = {
    frp {
      () =>
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