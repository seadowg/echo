package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import collection.mutable.Queue

trait Event[T] {
  protected def occs(time : Time) : Occurrence[T]

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
    
		    new EventView(mapFun)
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
    
		    new EventView(func)
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
  private var future : Queue[Occurrence[T]] = new Queue[Occurrence[T]]()
  private var present : Occurrence[T] = null
  private var length : BigInt = 0
  
  def event() : Event[T] = {
    new EventView(time => occs(time))
  }
  
  protected def occs(time : Time) : Occurrence[T] = {
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
  
  protected def occur(time : Time, value : T) {
    this synchronized {
			while (!writeLock.available) {}
	
      val nowCache = now()
      length += 1
      
      if (time < nowCache) {
        future += new Occurrence(nowCache, value, length)
      }

      else {
        future += new Occurrence(time, value, length)
      }
    }
  }
}

protected class EventView[T, U](private val source : Time => Occurrence[T]) extends Event[T] {
  protected def occs(time : Time) : Occurrence[T] = {
    source(time)
  }
}

class Occurrence[T](val time: Time, val value: T, val num : BigInt) {
  def map[U](func : T => U) : Occurrence[U] = {
    new Occurrence(time, func(value), num)
  }
}

object Event {
  def apply[T](time : Time, value : T) : Event[T] = {
    val source = new EventSource[T] {
      occur(time, value)
    }
    
    source.event()
  }
}