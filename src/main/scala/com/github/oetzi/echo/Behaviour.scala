import java.util.Date

package com.github.oetzi.echo {
	class Behaviour[T](rule : Double => T) {
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
		
		def until[A](event : Event[A], rule : Double => T) : Behaviour[T] = {
			new Behaviour[T](this.rule)
		}
	}
}