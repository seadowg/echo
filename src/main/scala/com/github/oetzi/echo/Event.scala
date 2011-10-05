package com.github.oetzi.echo {
	trait EventSource[T] {
		var edges : List[T => Any] = List[T => Any]()
		
		def occur(event : T) {
			edges.foreach(edge => edge(event))
		}
		
		def each(edge : T => Any) {
			edges = edges ++ List[T => Any](edge)
		}
		
		def ++(event : EventSource[T]) : EventSource[T] = {
			val new_event = new Event[T]
			this.each(e => new_event.occur(e))
			event.each(e => new_event.occur(e))
			return new_event
		}
	}
	
	class Event[T] extends EventSource[T] {	}
}