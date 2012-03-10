package com.github.oetzi.echo

import com.github.oetzi.echo.core._
import concurrent.Lock
import java.lang.IllegalAccessException

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

  implicit def lift[T](value: T): Behavior[T] = new Constant(value)

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

private[echo] object Control {
	val readLock = new Lock()
	val writeLock = new Lock()
	val createLock = new Lock()
	private var devModeOn = false
	
	def devMode() {
		devModeOn = true
	}
	
	def frp[T](block : () => T) : T = {
		if (createLock.available && !devModeOn) {
			throw new IllegalAccessException("You can't do that...")
		}
		
		else {
			block()
		}
	}
}