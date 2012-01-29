package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.core._
import com.github.oetzi.echo.types.Switcher


object SwitcherSpec extends Specification {
  "Switcher" should {
    "have an at" >> {
      "returning b.at for initial behaviour b for empty event" in {
        val behaviour = new Behaviour(time => 5)
        val switcher = new Switcher(behaviour, new Event[Behaviour[Int]])

        switcher.at(0) mustBe 5
      }

      "returning b.at for initial behaviour b if time is before first event occurrence" in {
        val behaviour = new Behaviour(time => 5)
        val event = new Event[Behaviour[Int]]
        val switcher = new Switcher(behaviour, event)
        event.occur(new Occurrence(5, new Behaviour(time => 6)))

        switcher.at(4) mustBe 5
      }

      "returning b.at for the newest behaviour if time is after last event" in {
        val behaviour = new Behaviour(time => 5)
        val event = new Event[Behaviour[Int]]
        val switcher = new Switcher(behaviour, event)
        event.occur(new Occurrence(5, new Behaviour(time => 6)))

        switcher.at(6) mustBe 6
      }

      "returning b.at for a behaviour thats occurrence matches time" in {
        val behaviour = new Behaviour(time => 5)
        val event = new Event[Behaviour[Int]]
        val switcher = new Switcher(behaviour, event)
        event.occur(new Occurrence(5, new Behaviour(time => 6)))
        event.occur(new Occurrence(6, new Behaviour(time => 7)))

        switcher.at(5) mustBe 6
      }

      "returning b.at for a the newest behaviour thats occurrence is less than time" in {
        val behaviour = new Behaviour(time => 5)
        val event = new Event[Behaviour[Int]]
        val switcher = new Switcher(behaviour, event)
        event.occur(new Occurrence(5, new Behaviour(time => 6)))
        event.occur(new Occurrence(7, new Behaviour(time => 7)))

        switcher.at(6) mustBe 6
      }
    }
  }
}