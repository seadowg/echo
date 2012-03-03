package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._
import collection.mutable.Queue

trait Event[T] {
  protected def occs(time : Time) : Occurrence[T]

  def map[U](func : T => U) : Event[U] = {
    new EventView(time => occs(time).map(func))
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
  private var length = 0
  
  def event() : Event[T] = {
    new EventView(time => occs(time))
  }
  
  protected def occs(time : Time) : Occurrence[T] = {
    this synchronized {
      if (future.length > 0) {
        val head = future.head

        if (head.time <= time) {
          present = future.dequeue()
        }
      }
      
      present
    }
  }
  
  protected def occur(time : Time, value : T) {
    this synchronized {
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
    this synchronized {
      source(time)
    }
  }
}

class Occurrence[T](val time: Time, val value: T, val num : Int) {
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