import com.github.oetzi.echo.core._
import com.github.oetzi.echo.util.NumBehaviour

package com.github.oetzi.echo {
	object Echo {
		implicit def lift[T](value : T) : Behaviour[T] = new Behaviour(time => value)
		implicit def beh2Num[T](behaviour : Behaviour[T]) : NumBehaviour[T] = new NumBehaviour[T](behaviour)
	}
}