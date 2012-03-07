package com.github.oetzi.echo

import com.github.oetzi.echo.core._

object Echo {
  private var fake = false
  private var fakeTime : Time = 0
  
  private def useRealTime() {
    this.fake = false
  }

  private def setTime(time : Time) {
    this.fake = true
    this.fakeTime = time
  }

  implicit def lift[T](value: T): Behavior[T] = new Behavior(time => value)

  type Time = Double

  def now() : Time = {
    if (this.fake) {
      this.fakeTime
    }

    else {
      System.currentTimeMillis
    }
  }
  
  def freezeTime[T](time : Time)(block : () => T) : T = {
    this.setTime(time)
    val returnValue = block()
    this.useRealTime()

    returnValue
  }
}