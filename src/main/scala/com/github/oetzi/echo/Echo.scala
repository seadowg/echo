package com.github.oetzi.echo

import com.github.oetzi.echo.core._
import concurrent.Lock
import java.lang.IllegalAccessException

object Echo {
  private var fake = 0
  private var fakeTime: Time = 0
  private var startTime: Time = 0

  /** Retuns the current Echo time. This
    * time is the time since the program started
    * running in nano seconds.
   */
  def now(): Time = {
    if (this.fake > 0) {
      this.fakeTime
    }

    else {
      System.nanoTime() - startTime
    }
  }

  /** Implicit function for lifiting static values
    * to Behaviours.
   */
  implicit def lift[T](value: T): Behaviour[T] = new Constant(value)

  /** Type alias for Time.
    */
  type Time = Double

  /** Starts the Echo clock at the beginning
    * of program execution.
   */
  private[echo] def startClock() {
    startTime = System.nanoTime()
  }

  /** Allows Echo's notion of time to be frozen
    * at the specified time for the given block's execution.
   */
  private[echo] def freezeTime[T](time: Time)(block: => T): T = {
    this.setTime(time)
    val returnValue = block
    this.useRealTime()

    returnValue
  }
  
  private def useRealTime() {
    this.fake -= 1
  }

  private def setTime(time: Time) {
    this.fake += 1
    this.fakeTime = time
  }
}

private[echo] object Control {
  val groupLock = new Lock()
  val createLock = new Lock()
  private var devModeOn = false

  /** Can be used to allow FRP code
    * to execute outside the EchoApp.setup
    * function (used for tests).
   */
  def devMode() {
    devModeOn = true
  }

  /** Functions that perform operations
    * on Behaviours or Events should have their
    * code wrapped in this block to circumvent
    * possible semantic problems.
   */
  def frp[T](block: => T): T = {
    if (createLock.available && !devModeOn) {
      throw new IllegalAccessException("You can't do that...")
    }

    else {
      block
    }
  }
}