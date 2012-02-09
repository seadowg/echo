import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {

import collection.mutable.ArrayBuffer

trait EventSource[T] {
  private var occurrences: ArrayBuffer[Occurrence[T]] = new ArrayBuffer[Occurrence[T]]()
  private var edges: List[Channel[T, _]] = Nil

  def occs(): List[Occurrence[T]] = {
    synchronized {
      occurrences.toList
    }
  }

  protected[echo] def lastValueAt(time: Time): Option[T] = {
    synchronized {
      val index = lastIndexAt(time).getOrElse(-1)

      if (index >= 0) {
        Some(occurrences(index).value)
      }

      else {
        None
      }
    }
  }

  protected[echo] def lastIndexAt(time: Time): Option[Int] = {
    synchronized {
      for (i <- occurrences.length - 1 to 0 by -1) {
        if (occurrences(i).time <= time) {
          return Some(i)
        }
      }

      None
    }
  }

  protected[echo] def occur(occurrence: Occurrence[T]) {
    synchronized {
      if (!occurrences.isEmpty && occurrence.time < occurrences.last.time) {
        for (i <- 0 until occurrences.length) {
          if (occurrence.time >= occurrences(i).time) {
            occurrences = occurrences.slice(0, i + 1) + occurrence ++
              occurrences.slice(i + 1, occurrences.length)
          }
        }
      }

      else {
        occurrences = occurrences + occurrence
      }

      echo(occurrence)
    }
  }

  def filter(func: Occurrence[T] => Boolean): Event[T] = {
    synchronized {
      val newEvent = new Event[T]
      newEvent.occurrences = this.occurrences.filter(func)
      this.addEdge(newEvent, func, occ => occ)
      newEvent
    }
  }

  def map[B](func: Occurrence[T] => Occurrence[B]): Event[B] = {
    synchronized {
      val newEvent = new Event[B]
      newEvent.occurrences = this.occurrences.map(func)
      this.addEdge(newEvent, occ => true, func)
      newEvent
    }
  }

  def mapV[B](func: T => B): Event[B] = {
    synchronized {
      this.map(occ => new Occurrence(occ.time, func(occ.value)))
    }
  }

  def mapT[B](func: Time => Time): Event[T] = {
    synchronized {
      this.map(occ => new Occurrence(func(occ.time), occ.value))
    }
  }

  def merge(event: EventSource[T]): Event[T] = {
    synchronized {
      val newEvent = new Event[T]
      newEvent.mergeList(this.occurrences)
      newEvent.mergeList(event.occurrences)
      this.addEdge(newEvent, occ => true, occ => occ)
      event.addEdge(newEvent, occ => true, occ => occ)
      newEvent
    }
  }

  private def addEdge[U](endPoint: EventSource[U], filter: Occurrence[T] => Boolean,
                         map: Occurrence[T] => Occurrence[U]) {
    this.edges = this.edges ++ List[Channel[T, U]](new Channel[T, U](endPoint, filter, map))
  }

  private def echo(occurrence: Occurrence[T]) {
    edges.foreach {
      channel =>
        channel.send(occurrence)
    }
  }

  private def mergeList(toMerge: ArrayBuffer[Occurrence[T]]) {
    var newList = new ArrayBuffer[Occurrence[T]]()
    var left = occurrences
    var right = toMerge

    while (!left.isEmpty || !right.isEmpty) {
      if (!left.isEmpty && !right.isEmpty) {
        if (left.head.time <= right.head.time) {
          newList = newList + left.head
          left = left.drop(1)
        }

        else {
          newList = newList + right.head
          right = right.drop(1)
        }
      }

      else if (!left.isEmpty) {
        newList = newList + left.head
        left = left.drop(1)
      }

      else if (!right.isEmpty) {
        newList = newList + right.head
        right = right.drop(1)
      }
    }

    occurrences = newList
  }
}

class Occurrence[T](val time: Time, val value: T) {}

class Channel[T, U](val endPoint: EventSource[U], val filter: Occurrence[T] => Boolean,
                    val map: Occurrence[T] => Occurrence[U]) {

  def send(occurrence: Occurrence[T]) {
    if (filter(occurrence)) {
      endPoint.occur(map(occurrence))
    }
  }
}

class Event[T] extends EventSource[T] {}

object Event {
  def apply[T]() = {
    new Event[T]
  }

  def apply[T](time: Time, value: T): EventSource[T] = {
    val event = new Event[T]
    event.occur(new Occurrence(time, value))
    event
  }

  def join[T, U[T] <: EventSource[T]](event: EventSource[U[T]]): EventSource[T] = {
    val newEvent: EventSource[T] = new Event[T]
    event.map {
      occEvent =>
        occEvent.value.map {
          occ =>
            val delayedOcc = new Occurrence(math.max(occEvent.time, occ.time), occ.value)
            newEvent.occur(delayedOcc)
            occ
        }
        occEvent
    }
    newEvent
  }
}

}