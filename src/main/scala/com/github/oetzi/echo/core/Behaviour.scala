import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.core {
	class Behaviour[T](private val rule : Time => T) {
		def now() : T = {
			this.at(System.currentTimeMillis)
		}
		
		def at(time : Time) : T = {
			rule(time)
		}
		
		def sample[A](event : EventSource[A]) : Event[T] = {
			val newEvent = new Event[T]
			
			event.foreach { occurence =>
				newEvent.occur(new Occurence(occurence.time, this.at(occurence.time)))
			}
			
			newEvent
		}
		
		def until[A](event : EventSource[A], newRule : Time => T) : Behaviour[T] = {
			val rule : Time => T = { time =>
				if (!event.occs.isEmpty && event.occs.first.time <= time) {
					newRule(time)
				}
				else {
					this.rule(time)	
				}
			}
			
			new Behaviour(rule)
		}
		
		def map[B](func : T => B) : Behaviour[B] = {
			new Behaviour(time => func(this.at(time)))
		}
		
		override def toString() : String = {
			this.now.toString
		}
	}
}