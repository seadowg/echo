import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.EmbdBehaviour

package com.github.oetzi.echo.util {
	class NumBehaviour[T](behaviour : Behaviour[T]) extends EmbdBehaviour[T](behaviour) {
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
	}
}