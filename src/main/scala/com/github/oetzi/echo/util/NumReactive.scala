import com.github.oetzi.echo.core.Continuous
import com.github.oetzi.echo.core.Continuous	
import com.github.oetzi.echo.core.Reactive
import com.github.oetzi.echo.core.EmbdContinuous

package com.github.oetzi.echo.util {
	class NumReactive[T](self : Continuous[T]) extends EmbdContinuous(self) {
		def +(continuous : Continuous[T])(implicit numeric : Numeric[T]) : Reactive[T] = {
			val new_rule : () => T = {
				() => numeric.plus(self.now, continuous.now)
			}
			
			new Reactive[T](new_rule)
		}
		
		def -(continuous : Continuous[T])(implicit numeric : Numeric[T]) : Reactive[T] = {
			val new_rule : () => T = {
				() => numeric.minus(self.now, continuous.now)
			}
			
			new Reactive[T](new_rule)
		}
		
		def *(continuous : Continuous[T])(implicit numeric : Numeric[T]) : Reactive[T] = {
			val new_rule : () => T = {
				() => numeric.times(self.now, continuous.now)
			}
			
			new Reactive[T](new_rule)
		}
	}
}