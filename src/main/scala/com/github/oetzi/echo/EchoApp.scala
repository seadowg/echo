package com.github.oetzi.echo

import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.Echo._
import collection.mutable.ArrayBuffer

/**EchoApp should be extended to create
 * Echo FRP applications. Programmer's simply
 * need to implement the setup method with their
 * FRP code and the predefined main() will
 * take care of everything else.
 */
abstract class EchoApp {
  def setup(args: Array[String])

  /**The main() function executes the user's
   * setup implementation, executes any callbacks
   * and then starts the Echo clock.
   */
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

  /**Allows functions to be set to execute after
   * the group of FRP objects are created in the
   * EchoApp.setup function. Functions
   * will execute in a frozen state at time 0.
   */
  private[echo] def afterSetup(block: () => Unit) {
    setupCallbacks += block
  }

  private def runAfterSetup() {
    setupCallbacks.foreach {
      block => block()
    }
  }
}

