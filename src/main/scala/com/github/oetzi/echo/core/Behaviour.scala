import com.github.oetzi.echo.Echo._

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
	
	class Behaviour[T](val rule : Time => T) extends Continuous[T]() {
		def now() : T = {
			this.at(System.currentTimeMillis)
		}
		
		def map[B](func : T => B) : Behaviour[B] = {
			new Behaviour(time => func(this.at(time)))
		}
		
		def at(time : Double) : T = {
			rule(time)
		}
	}
	
	class Reactive[T](val rule : () => T) extends Continuous[T]() {
		def now() : T = {
			rule()
		}
	}
	
	class EmbdContinuous[T](val self : Continuous[T]) { }
	
	case class NonDeterminismException() extends Exception { }
}