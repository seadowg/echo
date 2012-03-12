package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.test.help.TestEvent
import com.github.oetzi.echo.core.{Switcher, Behavior}


object SwitcherSpec extends Specification {

  devMode()

  "Switcher" should {
    "have an at" >> {
      "returning 'initial' if event hasn't occured" in {
        val switcher = new Switcher(0, new TestEvent[Behavior[Int]])

        switcher.eval() mustBe 0
      }

      "returning the newest event if time is >= last event occurrence" in {
        val event = new TestEvent[Behavior[Int]]
        event.pubOccur(5)
        val switcher = new Switcher(0, event)

        switcher.eval mustBe 5
      }

      "returning an event's value if the time is equal to it" in {
        val event = new TestEvent[Behavior[Int]]

        freezeTime(5) {
          () =>
            event.pubOccur(10)
            val switcher = new Switcher(0, event)
            switcher.eval()
        }.mustBe(10)
      }

      "returning an events value if it has the max before time" in {
        val event = new TestEvent[Behavior[Int]]

        freezeTime(5) {
          () => event.pubOccur(10)
        }

        freezeTime(6) {
          () =>
            val switcher = new Switcher(0, event)
            switcher.eval()
        }.mustBe(10)
      }
    }
  }
}