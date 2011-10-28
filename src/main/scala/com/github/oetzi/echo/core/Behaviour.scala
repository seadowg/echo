import java.util.Date

package com.github.oetzi.echo.core {
	class Behaviour[T](var rule : Double => T) {
		def now() : T = {
			rule(System.currentTimeMillis)
		}
		
		def +(behaviour : Behaviour[T])(implicit numeric : Numeric[T]) : Behaviour[T] = {
			val new_rule : Double => T = {
				time => numeric.plus(this.now, behaviour.now)
			}
			
			new Behaviour[T](new_rule)
		}
		
		def -(behaviour : Behaviour[T])(implicit numeric : Numeric[T]) : Behaviour[T] = {
			val new_rule : Double => T = {
				time => numeric.minus(this.now, behaviour.now)
			}
			
			new Behaviour[T](new_rule)
		}
		
		def *(behaviour : Behaviour[T])(implicit numeric : Numeric[T]) : Behaviour[T] = {
			val new_rule : Double => T = {
				time => numeric.times(this.now, behaviour.now)
			}
			
			new Behaviour[T](new_rule)
		}
		
		def change(rule : Double => T) = {
			this.rule = rule
			this
		}
		
		def until[A](event : EventSource[A], rule : Double => T) : Behaviour[T] = {
			val beh = new Behaviour(this.rule)
			event.foreach(occur => beh.change(rule))
			beh
		}
		
		def sample[A](event : EventSource[A]) : EventSource[T] = {
			val newEvent = new Event[T]
			event.foreach(event => newEvent.occur(this.now))
			return newEvent
		}
		
		override def toString() : String = {
			this.now.toString
		}
	}
	
	class Stepper[T](val init : T, val event : Event[T]) extends Behaviour[T](time => init) {
		event.foreach(newValue => this.change(time => newValue))
	}
	
	class EmbdBehaviour[T](behaviour : Behaviour[T]) extends Behaviour[T](time => behaviour.now) {
		
	}
}