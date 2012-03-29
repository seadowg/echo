package com.github.oetzi.echo

import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.Echo._
import collection.mutable.ArrayBuffer

abstract class EchoApp {  
  def setup(args: Array[String])

  def main(args: Array[String]) {
    groupLock synchronized {
      freezeTime(0) {
        createLock.acquire()
        setup(args)
        createLock.release()
        
        EchoApp.runAfterSetup()
      }
      
      startClock()
    }
  }
}

object EchoApp {
  private val setupCallbacks = new ArrayBuffer[() => Unit]
  
  private[echo] def afterSetup(block : () => Unit) {
    setupCallbacks + block
  }
  
  private def runAfterSetup() {
    setupCallbacks.foreach {
      block => block()
    }
  }
}

