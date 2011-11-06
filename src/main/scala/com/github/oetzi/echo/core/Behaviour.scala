import java.util.Date

package com.github.oetzi.echo.core {
	abstract class Continuous[T] {
		def now : T
		
		def sample[A](event : EventSource[A]) : EventSource[T] = {
			val newEvent = new Event[T]
			event.foreach(event => newEvent.occur(this.now))
			return newEvent
		}
		
		override def toString() : String = {
			this.now.toString
		}
	}
	
	class Behaviour[T](var rule : Double => T) extends Continuous[T]() {
		def now() : T = {
			rule(System.currentTimeMillis)
		}
		
		def at(time : Double) : T = {
			rule(time)
		}
		
		def change(rule : Double => T) : Behaviour[T] = {
			this.rule = rule
			this
		}
		
		def until[A](event : EventSource[A], rule : Double => T) : Behaviour[T] = {
			val beh = new Behaviour(this.rule)
			event.foreach(occur => beh.change(rule))
			beh
		}
	}
	
	class Reactive[T](var rule : () => T) extends Continuous[T]() {
		def now() : T = {
			rule()
		}
	}
	
	class Stepper[T](val init : T, val event : Event[T]) extends Behaviour[T](time => init) {
		event.foreach(newValue => this.change(time => newValue))
		
		override def at(time : Double) : T = {
			throw new NonDeterminismException()
		}
	}
	
	class EmbdContinuous[T](val self : Continuous[T]) { }
	
	case class NonDeterminismException() extends Exception { }
}