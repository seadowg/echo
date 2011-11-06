import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.Continuous	
import com.github.oetzi.echo.core.Reactive
import com.github.oetzi.echo.core.EmbdContinuous

package com.github.oetzi.echo.util {
	class NumBehaviour[T](self : Behaviour[T]) extends EmbdContinuous[T](self) {
		def +(behaviour : Behaviour[T])(implicit numeric : Numeric[T]) : Behaviour[T] = {
			val new_rule : Double => T = {
				time => numeric.plus(self.at(time), behaviour.at(time))
			}
			
			new Behaviour[T](new_rule)
		}
		
		def -(behaviour : Behaviour[T])(implicit numeric : Numeric[T]) : Behaviour[T] = {
			val new_rule : Double => T = {
				time => numeric.minus(self.at(time), behaviour.at(time))
			}
			
			new Behaviour[T](new_rule)
		}
		
		def *(behaviour : Behaviour[T])(implicit numeric : Numeric[T]) : Behaviour[T] = {
			val new_rule : Double => T = {
				time => numeric.times(self.at(time), behaviour.at(time))
			}
			
			new Behaviour[T](new_rule)
		}
		
		def +(reactive : Reactive[T])(implicit numeric : Numeric[T]) : Reactive[T] = {
			val new_rule : () => T = {
				() => numeric.plus(self.now, reactive.now)
			}
			
			new Reactive[T](new_rule)
		}
		
		def -(reactive : Reactive[T])(implicit numeric : Numeric[T]) : Reactive[T] = {
			val new_rule : () => T = {
				() => numeric.minus(self.now, reactive.now)
			}
			
			new Reactive[T](new_rule)
		}
		
		def *(reactive : Reactive[T])(implicit numeric : Numeric[T]) : Reactive[T] = {
			val new_rule : () => T = {
				() => numeric.times(self.now, reactive.now)
			}
			
			new Reactive[T](new_rule)
		}
	}
}