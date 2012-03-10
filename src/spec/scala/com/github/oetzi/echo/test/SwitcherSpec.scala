package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.core.Behavior
import com.github.oetzi.echo.test.help.TestEvent
import com.github.oetzi.echo.types.Switcher


object SwitcherSpec extends Specification {
	
	devMode()
	
  "Switcher" should {
    "have an at" >> {
      "returning 'initial' if event hasn't occured" in {
        val stepper = new Switcher(0, new TestEvent[Behavior[Int]])

        stepper.eval() mustBe 0
      }

      "returning the newest event if time is >= last event occurrence" in {
        val event = new TestEvent[Behavior[Int]]
        event.pubOccur(now(), 5)
        val stepper = new Switcher(0, event)

        stepper.eval mustBe 5
      }

      "returning the initial value if the time is < the first event" in {
        val event = new TestEvent[Behavior[Int]]

        val result = freezeTime(0) {
          () =>
            event.pubOccur(1, 5)
            val stepper = new Switcher(0, event)
            stepper.eval()
        }

        result mustBe 0
      }

      "returning an event's value if the time is equal to it" in {
        val event = new TestEvent[Behavior[Int]]

        freezeTime(5) {
          () =>
            event.pubOccur(5, 10)
            event.pubOccur(7, 11)
            val stepper = new Switcher(0, event)
            stepper.eval()
        }.mustBe(10)
      }

      "returning an events value if it has the max before time" in {
        val event = new TestEvent[Behavior[Int]]

        freezeTime(5) {
          () =>
            event.pubOccur(5, 10)
            event.pubOccur(6, 9)
            event.pubOccur(8, 11)
            val stepper = new Switcher(0, event)
            stepper.eval()
        }.mustBe(10)
      }
    }
  }
}