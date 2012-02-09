package com.github.oetzi.echo

import com.github.oetzi.echo.core._

object Echo {
  implicit def lift[T](value: T): Behaviour[T] = new Behaviour(time => value)

  type Time = Double

  def now = System.currentTimeMillis
}