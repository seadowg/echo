import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {
	trait EventSource[T] {
		private var occurences : List[Occurrence[T]] = List[Occurrence[T]]()
		private var edges : List[Channel[T, _]] = Nil
		private var ops : List[Occurrence[T] => Any] = List[Occurrence[T] => Any]()
		
		def occs() : List[Occurrence[T]] = {
			synchronized {
				occurences
			}
		}
		
		def occAt(time : Time) : Option[Occurrence[T]] = {
			synchronized {
				if (occs.isEmpty) {
						return None
				}
				
				val filtered = this.occs.filter(occ => occ.time == time)
				
				if (filtered.isEmpty) {
					None
				}
				
				else {
					Some(filtered.last)
				}
			}
		}
		
		def occsBefore(time : Time) : List[Occurrence[T]] = {
			synchronized {
				time match {
					case empty if occs.isEmpty => List[Occurrence[T]]()
					case less if less <= this.occs.first.time => List[Occurrence[T]]()
					case all if all > this.occs.last.time => this.occs
					case _ => this.occs.filter(occ => occ.time < time	)
				}
			}
		}
		
		def occur(occurence : Occurrence[T]) {
			synchronized {
				if (!occurences.isEmpty && occurence.time < occurences.last.time) {
					for (i <- 0 until occurences.length) {
						if (occurence.time >= occurences(i).time) {
							occurences = occurences.slice(0, i + 1) ++ List(occurence) ++ occurences.slice(i + 1, occurences.length)
						}
					}
				}
				
				else {
					occurences = occurences ++ List(occurence)
				}
				
				echo(occurence)
				ops.foreach { op =>
					op(occurence)
				}
			}
		}
		
		def foreach(func : Occurrence[T] => Any) {
			synchronized {
				ops = ops ++ List(func)
				occurences.foreach { occ =>
					func(occ)
				}
			}
		}
		
		def filter(func : Occurrence[T] => Boolean) = {
			synchronized {
				val newEvent = new Event[T]
				newEvent.occurences = this.occurences.filter(func)
				this.addEdge(newEvent, func, occ => occ)
				newEvent
			}
		}
		
		def map[B](func : Occurrence[T] => Occurrence[B]) : EventSource[B] = {
			synchronized {
				val newEvent = new Event[B]
				newEvent.occurences = this.occurences.map(func)
				this.addEdge(newEvent, occ => true, func)
				newEvent
			}
		}
		
		def merge(event : EventSource[T]) : EventSource[T] = {
			synchronized {
				val newEvent = new Event[T]
				newEvent.mergeList(this.occs)
				newEvent.mergeList(event.occs)
				this.addEdge(newEvent, occ => true, occ => occ)
				event.addEdge(newEvent, occ => true, occ => occ)
				newEvent
			}
		}
		
		private def addEdge[U](endPoint : EventSource[U], filter : Occurrence[T] => Boolean, 
			map : Occurrence[T] => Occurrence[U]) {
			this.edges = this.edges ++ List[Channel[T, U]](new Channel[T, U](endPoint, filter, map))
		}
		
		private def echo(occurence : Occurrence[T])
		{
			edges.foreach { channel => 
				channel.send(occurence)
			}
		}
		
		private def mergeList(toMerge : List[Occurrence[T]]) {
			var newList = List[Occurrence[T]]()
			var left = occurences
			var right = toMerge
			
			while (!left.isEmpty || !right.isEmpty) {
				if (!left.isEmpty && !right.isEmpty) {
					if (left.first.time <= right.first.time) {
						newList = newList ++ List(left.first)
						left = left.drop(1)
					}
					
					else {
						newList = newList ++ List(right.first)
						right = right.drop(1)
					}
				}
				
				else if (!left.isEmpty) {
					newList = newList ++ List(left.first)
					left = left.drop(1)
				}
				
				else if (!right.isEmpty) {
					newList = newList ++ List(right.first)
					right = right.drop(1)
				}
			}
			
			occurences = newList
		}
	}
	
	class Occurrence[T](val time : Time, val value : T) { }
	
	class Channel[T, U](val endPoint : EventSource[U], val filter : Occurrence[T] => Boolean,
		val map : Occurrence[T] => Occurrence[U]) {
		
		def send(occurence : Occurrence[T]) {
			if (filter(occurence)) {
				endPoint.occur(map(occurence))
			}
		}
	}
	
	class Event[T] extends EventSource[T] { }
	
	object Event {
		def apply[T]() = {
			new Event[T]
		}
		
		def apply[T](time : Time, value : T) = {
			val event = new Event[T]
			event.occur(new Occurrence(time, value))
			event
		}
	}
}