package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import collection.mutable.Queue
import collection.mutable.ArrayBuffer

trait Event[T] {
  protected def occs(time : Time) : Occurrence[T]
	protected[echo] def hook(block : Occurrence[T] => Unit)

  def map[U](func : T => U) : Event[U] = {
		frp {
			() =>
    		val mapFun : Time => Occurrence[U] = {
		      time =>
		        val occ = occs(time)
        
		        if (occ == null) {
		          null
		        }

		        else {
		          occs(time).map(func)
		        }
		    }
		
				val jsThisTrick = this
    
		    new Event[U] {
					protected def occs(time : Time) : Occurrence[U] = {
						mapFun(time)
					}
					
					protected[echo] def hook(block : Occurrence[U] => Unit) {
						val mapBlock : Occurrence[T] => Unit = {
							occ => block(occ.map(func))
						}
						
						jsThisTrick.hook(mapBlock)
					}
				}
		}
  }
  
  def merge(event : Event[T]) : Event[T] = {
		frp {
			() =>
    		val func : Time => Occurrence[T] = {
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
					protected def occs(time : Time) : Occurrence[T] = {
						func(time)
					}
					
					protected[echo] def hook(block : Occurrence[T] => Unit) {
						jsThisTrick.hook(block)
						event.hook(block)
					}
				}
		}
  }
  
  // echo utility functions
  def top(time : Time) : Option[Occurrence[T]] = {
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
	val hooks : ArrayBuffer[Occurrence[T] => Unit] = new ArrayBuffer() 
  private var present : Occurrence[T] = null
  private var length : BigInt = 0
  
  def event() : Event[T] = {
		val jsThisTrick = this
		
    new Event[T] {
			protected def occs(time : Time) : Occurrence[T] = {
				jsThisTrick.occs(time)
			}
			
			protected[echo] def hook(block : Occurrence[T] => Unit) {
				jsThisTrick.hook(block)
			}
		}
  }
  
  protected def occs(time : Time) : Occurrence[T] = {
    this synchronized {
      present
    }
  }
  
  protected def occur(value : T) {
    this synchronized {
			writeLock synchronized {
				while(!createLock.available) {}
				
				freezeTime(now()) {
					() =>
			  		length += 1
		   			present = new Occurrence(now(), value, length)

						hooks.foreach {
							block => block(present)
						}
				}
			}
    }
  }

	protected[echo] def hook(block :	Occurrence[T] => Unit) {
		hooks += block
	}
}

class Occurrence[T](val time: Time, val value: T, val num : BigInt) {
  def map[U](func : T => U) : Occurrence[U] = {
    new Occurrence(time, func(value), num)
  }
}