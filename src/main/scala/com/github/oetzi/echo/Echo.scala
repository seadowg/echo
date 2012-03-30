package com.github.oetzi.echo

import com.github.oetzi.echo.core._
import concurrent.Lock
import java.lang.IllegalAccessException

object Echo {
  private var fake = 0
  private var fakeTime: Time = 0
  private var startTime: Time = 0

  def now(): Time = {
    if (this.fake > 0) {
      this.fakeTime
    }

    else {
      System.nanoTime() - startTime
    }
  }

  implicit def lift[T](value: T): Behaviour[T] = new Constant(value)

  type Time = Double

  private[echo] def startClock() {
    startTime = System.nanoTime()
  }

  private def useRealTime() {
    this.fake -= 1
  }

  private def setTime(time: Time) {
    this.fake += 1
    this.fakeTime = time
  }

  private[echo] def freezeTime[T](time: Time)(block: => T): T = {
    this.setTime(time)
    val returnValue = block
    this.useRealTime()

    returnValue
  }
}

private[echo] object Control {
  val groupLock = new Lock()
  val createLock = new Lock()
  private var devModeOn = false

  def devMode() {
    devModeOn = true
  }

  def frp[T](block: => T): T = {
    if (createLock.available && !devModeOn) {
      throw new IllegalAccessException("You can't do that...")
    }

    else {
      block
    }
  }
}