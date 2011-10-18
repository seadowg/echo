import java.util.Date

package com.github.oetzi.echo {
	class Behaviour[T](var rule : Double => T) {
		var witness : Witness[T] = null;
		
		def now() : T = {
			rule(new Date().getTime)
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
		
		def sample() : Witness[T] = {
			if (witness == null) this.witness = new Witness(this)
			this.witness
		}
		
		override def toString() : String = {
			this.now.toString
		}
	}
	
	class Stepper[T](val init : T, val event : Event[T]) extends Behaviour[T](time => init) {
		event.foreach(newValue => this.change(time => newValue))
	}
}