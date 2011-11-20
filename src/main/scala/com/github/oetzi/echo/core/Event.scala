import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {
	trait EventSource[T] {
		var list : List[Occurence[T]] = List[Occurence[T]]()
		var edges : Set[EventSource[T]] = Set[EventSource[T]]()
		var ops : List[Occurence[T] => Any] = List[Occurence[T] => Any]()
		
		def occs() : List[Occurence[T]] = {
			synchronized {
				list
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
				list.foreach { occ =>
					func(occ)
				}
			}
		}
		
		def occur(occurence : Occurence[T]) {
			synchronized {
				if (!list.isEmpty && occurence.time < list.last.time) {
					for (i <- 0 until list.length) {
						if (occurence.time >= list(i).time) {
							list = list.slice(0, i + 1) ++ List(occurence) ++ list.slice(i + 1, list.length)
						}
					}
				}
				
				else {
					list = list ++ List(occurence)
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
			edges.foreach { event => 
				event.occur(occurence)
			}
		}
		
		private def addEdge(event : EventSource[T]) {
			edges = edges + event
		}
		
		private def mergeList(toMerge : List[Occurence[T]]) {
			var newList = List[Occurence[T]]()
			var left = list
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
			
			list = newList
		}
	}
	
	class Occurence[T](val time : Time, val value : T) { }
	
	class Event[T] extends EventSource[T] {	}
}