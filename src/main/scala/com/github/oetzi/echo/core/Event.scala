import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {
	class Occurence[T](val time : Time, val value : T) { }
	
	trait EventSource[T] {
		var list : List[Occurence[T]] = List[Occurence[T]]()
		
		def occs() : List[Occurence[T]] = {
			list
		}
		
		def occur(occurence : Occurence[T]) {
			list = list ++ List(occurence)
		}
		
		def merge(event : EventSource[T]) : EventSource[T] = {
			val newEvent = new Event[T]
			newEvent.mergeList(this.occs)
			newEvent.mergeList(event.occs)
			newEvent
		}
		
		private def mergeList(toMerge : List[Occurence[T]]) {
			list = list ++ toMerge
		}
	}
	
	class Event[T] extends EventSource[T] {	}
}