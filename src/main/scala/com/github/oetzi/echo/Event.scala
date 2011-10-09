package com.github.oetzi.echo {
	trait EventSource[T] {
		var edges : List[T => Any] = List[T => Any]()
		
		def apply(matcher : T) : EventSource[T]= {
			this.filter(_ == matcher)
		}
		
		def occur(event : T) {
			edges.foreach(edge => edge(event))
		}
		
		def each(edge : T => Any) {
			edges = edges ++ List[T => Any](edge)
		}
		
		def filter(predicate : T => Boolean) : EventSource[T] = {
			val new_event = new Event[T]
			this.each(event => if (predicate(event)) new_event.occur(event))
			return new_event
		}
		
		def ++(event : EventSource[T]) : EventSource[T] = {
			val new_event = new Event[T]
			this.each(e => new_event.occur(e))
			event.each(e => new_event.occur(e))
			return new_event
		}
	}
	
	class Event[T] extends EventSource[T] {	}
	
	class Witness[T](val behaviour : Behaviour[T]) extends EventSource[T] {
		var shouldRun = true
		var thread = new Thread(new Runnable() {
			def run() {
				var last : Any = null
				while (shouldRun) {
					val now = behaviour.now
					if (last != now) {
						Witness.this.occur(now)
						last = now
					}
				}
			}
		})
		thread.start
		
		def dispose() {
			shouldRun = false
			thread = null
		}
	}
}