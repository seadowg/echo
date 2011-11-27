import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {
	trait EventSource[T] {
		private var occurences : List[Occurence[T]] = List[Occurence[T]]()
		private var edges : List[Channel[T]] = List[Channel[T]]()
		private var ops : List[Occurence[T] => Any] = List[Occurence[T] => Any]()
		
		def occs() : List[Occurence[T]] = {
			synchronized {
				occurences
			}
		}
		
		def occAt(time : Time) : Option[Occurence[T]] = {
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
		
		def occsBefore(time : Time) : List[Occurence[T]] = {
			synchronized {
				time match {
					case empty if occs.isEmpty => List[Occurence[T]]()
					case less if less <= this.occs.first.time => List[Occurence[T]]()
					case all if all > this.occs.last.time => this.occs
					case _ => this.occs.filter(occ => occ.time < time	)
				}
			}
		}
		
		def foreach(func : Occurence[T] => Any) {
			synchronized {
				ops = ops ++ List(func)
				occurences.foreach { occ =>
					func(occ)
				}
			}
		}
		
		def filter(func : Occurence[T] => Boolean) = {
			synchronized {
				val newEvent = new Event[T]
				newEvent.occurences = this.occurences.filter(func)
				this.addEdge(newEvent, func)
				newEvent
			}
		}
		
		def occur(occurence : Occurence[T]) {
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
		
		def merge(event : EventSource[T]) : EventSource[T] = {
			synchronized {
				val newEvent = new Event[T]
				newEvent.mergeList(this.occs)
				newEvent.mergeList(event.occs)
				this.addEdge(newEvent)
				event.addEdge(newEvent)
				newEvent
			}
		}
		
		private def echo(occurence : Occurence[T])
		{
			edges.foreach { channel => 
				channel.send(occurence)
			}
		}
		
		private def addEdge(event : EventSource[T]) {
			edges = edges ++ List(new Channel(event))
		}
		
		private def addEdge(event : EventSource[T], filter : Occurence[T] => Boolean) {
			edges = edges ++ List(new Channel(event, filter))
		}
		
		private def mergeList(toMerge : List[Occurence[T]]) {
			var newList = List[Occurence[T]]()
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
	
	class Occurence[T](val time : Time, val value : T) { }
	
	class Channel[T](val endPoint : EventSource[T], val filter : Occurence[T] => Boolean) {
		def this(endPoint : EventSource[T]) {
			this(endPoint, occur => true)
		}
		
		def send(occurence : Occurence[T]) {
			if (filter(occurence)) {
				endPoint.occur(occurence)
			}
		}
	}
	
	class Event[T] extends EventSource[T] {	}
}