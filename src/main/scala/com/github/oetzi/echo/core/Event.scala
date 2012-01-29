import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {

trait EventSource[T] {
  private var occurrences: List[Occurrence[T]] = Nil
  private var edges: List[Channel[T, _]] = Nil

  def occs(): List[Occurrence[T]] = {
    synchronized {
      occurrences
    }
  }

  def occAt(time: Time): Option[Occurrence[T]] = {
    synchronized {
      val filtered = this.occs().filter(occ => occ.time == time)

      if (filtered.isEmpty) {
        None
      }

      else {
        Some(filtered.last)
      }
    }
  }

  def occsBefore(time: Time): List[Occurrence[T]] = {
    synchronized {
      this.occs().filter(occ => occ.time < time)
    }
  }

  protected[echo] def occur(occurrence: Occurrence[T]) {
    synchronized {
      if (!occurrences.isEmpty && occurrence.time < occurrences.last.time) {
        for (i <- 0 until occurrences.length) {
          if (occurrence.time >= occurrences(i).time) {
            occurrences = occurrences.slice(0, i + 1) ++ List(occurrence) ++
              occurrences.slice(i + 1, occurrences.length)
          }
        }
      }

      else {
        occurrences = occurrences ++ List(occurrence)
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

  def merge(event: EventSource[T]): Event[T] = {
    synchronized {
      val newEvent = new Event[T]
      newEvent.mergeList(this.occs())
      newEvent.mergeList(event.occs())
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

  private def mergeList(toMerge: List[Occurrence[T]]) {
    var newList = List[Occurrence[T]]()
    var left = occurrences
    var right = toMerge

    while (!left.isEmpty || !right.isEmpty) {
      if (!left.isEmpty && !right.isEmpty) {
        if (left.head.time <= right.head.time) {
          newList = newList ++ List(left.head)
          left = left.drop(1)
        }

        else {
          newList = newList ++ List(right.head)
          right = right.drop(1)
        }
      }

      else if (!left.isEmpty) {
        newList = newList ++ List(left.head)
        left = left.drop(1)
      }

      else if (!right.isEmpty) {
        newList = newList ++ List(right.head)
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