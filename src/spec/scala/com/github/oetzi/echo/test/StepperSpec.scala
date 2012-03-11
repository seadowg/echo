package com.github.oetzi.echo.test

import help.TestEvent
import org.specs._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.types.Stepper

object StepperSpec extends Specification {
	
	devMode()
	
  "Stepper" should {
    "have an at" >> {
      "returning 'initial' if event hasn't occured" in {
        val stepper = new Stepper(0, new TestEvent[Int])

        stepper.eval() mustBe 0
      }

      "returning the newest event if time is >= last event occurrence" in {
        val event = new TestEvent[Int]
        event.pubOccur(5)
        val stepper = new Stepper(0, event)

        stepper.eval mustBe 5
      }

      "returning an event's value if the time is equal to it" in {
        val event = new TestEvent[Int]

        freezeTime(5) {
          () =>
            event.pubOccur(10)
            val stepper = new Stepper(0, event)
            stepper.eval()
        }.mustBe(10)
      }

      "returning an events value if it has the max before time" in {
        val event = new TestEvent[Int]

				freezeTime(5) { () => event.pubOccur(10) }

        freezeTime(6) {
          () =>
            val stepper = new Stepper(0, event)
            stepper.eval()
        }.mustBe(10)
      }
    }
  }
}