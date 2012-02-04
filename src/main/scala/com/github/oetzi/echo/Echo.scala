import com.github.oetzi.echo.core._

package com.github.oetzi.echo {

object Echo {
  implicit def lift[T](value: T): Behaviour[T] = new Behaviour(time => value)

  type Time = Double

  def now = System.currentTimeMillis
}

}