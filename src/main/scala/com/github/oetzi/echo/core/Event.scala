package com.github.oetzi.echo.core

import collection.Seq
import collection.mutable.ArrayBuffer

import com.github.oetzi.echo.Echo._

trait Event[T] {
  protected def occs() : Seq[Occurrence[T]]

  def map[U](func : T => U) : Event[U] = {
    new EventView(() => occs().map {
      occ =>
        new Occurrence(occ.time, func(occ.value))
    })
  }
  
  def filter(func : Occurrence[T] => Boolean) = {
    new EventView(() => occs().filter {
      occ =>
        func(occ)
    })
  }
  
  // echo utility functions
  private[echo] def head() : Option[Occurrence[T]] = {
    occs().headOption
  }
  
  private[echo] def lastValueAt(time : Time) : Option[T] = {
    occs().map(occ => occ.value).lastOption
  }
  
  private[echo] def lengthAt(time : Time) : Int = {
    occs().filter(occ => occ.time <= time).length
  }
}

trait EventSource[T] extends Event[T] {
  private val occsList = new ArrayBuffer[Occurrence[T]]
  
  def event() : Event[T] = {
    new EventView(() => occs())
  }
  
  protected def occs() : Seq[Occurrence[T]] = {
    this synchronized {
      this.occsList.toArray.view
    }
  }
  
  protected def occur(time : Time, value : T) {
    this synchronized {
      val nowCache = now()
      
      if (time < nowCache) {
        occsList += new Occurrence(nowCache, value)
      }

      else {
        occsList += new Occurrence(time, value)
      }
    }
  }
}

protected class EventView[T, U](private val source : () => Seq[Occurrence[T]]) extends Event[T] {
  protected def occs() : Seq[Occurrence[T]] = {
    this synchronized {
      source()
    }
  }
}

class Occurrence[T](val time: Time, val value: T) { }

object Event {
  def apply[T](time : Time, value : T) : Event[T] = {
    val source = new EventSource[T] {
      occur(time, value)
    }
    
    source.event()
  }
}