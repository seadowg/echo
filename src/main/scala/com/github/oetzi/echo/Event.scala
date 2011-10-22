package com.github.oetzi.echo {
	trait EventSource[T] {
		var edges : List[T => Any] = List[T => Any]()
		
		def apply(matcher : T) : EventSource[T]= {
			this.filter(_ == matcher)
		}
		
		def occur(event : T) {
			edges.foreach(edge => edge(event))
		}
		
		def foreach(edge : T => Any) {
			edges = edges ++ List[T => Any](edge)
		}
		
		def filter(predicate : T => Boolean) : EventSource[T] = {
			val new_event = new Event[T]
			this.foreach(event => if (predicate(event)) new_event.occur(event))
			return new_event
		}
		
		def ++(event : EventSource[T]) : EventSource[T] = {
			val new_event = new Event[T]
			this.foreach(e => new_event.occur(e))
			event.foreach(e => new_event.occur(e))
			return new_event
		}
	}
	
	class Event[T] extends EventSource[T] {	}
}