package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.Occurrence

object BehaviourSpec extends Specification {
  "Behaviour" should {
    "create a new instance given a valid block" in {
      val beh = new Behaviour(time => time)
      beh must_!= null
    }

    "be created from from Behaviour.apply" in {
      val behaviour = Behaviour(time => 5)
      behaviour.isInstanceOf[Behaviour[Int]] mustBe true
    }

    "provide an at function" >> {
      "returning the result of the rule with the passed value" in {
        val beh = new Behaviour(time => time * 5)
        beh.at(5).asInstanceOf[Int] mustBe 25
      }
    }

    "provide an until(non-relative) function" >> {
      "returning a new Behaviour" in {
        val beh = new Behaviour(time => 5)

        beh.until(new Event[Int], new Behaviour(time => 5)) must_!= beh
      }

      "returning a new Behaviour with the current rule when the Event hasn't occured" in {
        val beh = new Behaviour(time => 5)

        beh.until(new Event[Int], new Behaviour(time => 10)).at(0) mustBe 5
      }

      "returning a new Behaviour with the new rule after the Event occurs" in {
        var beh = new Behaviour(time => 5)
        val event = new Event[Int]
        beh = beh.until(event, new Behaviour(time => 10))
        event.occur(new Occurrence(1, 5))

        beh.at(1) mustBe 10
      }

      "returning a Behaviour thats rule only 'changes' if the time is after the event" in {
        val event = new Event[Int]
        val beh = new Behaviour(time => 5).until(event, new Behaviour(time => 10))
        event.occur(new Occurrence(2, 5))

        beh.at(1) mustBe 5
      }
    }

    "provide an until(relative) function" >> {
      "returning a new Behaviour" in {
        val beh = new Behaviour(time => 5)

        beh.until(0L, new Event[Int], new Behaviour(time => 5)) must_!= beh
      }

      "returning a new Behaviour with the current rule when the Event hasn't occured" in {
        val beh = new Behaviour(time => 5)

        beh.until(0L, new Event[Int], new Behaviour(time => 10)).at(1) mustBe 5
      }

      "returning a new Behaviour with the current rule when the Event has occurred before the time" in {
        val beh = new Behaviour(time => 5)

        beh.until(1L, Event[Int](0L, 0), new Behaviour(time => 10)).at(1) mustBe 5
      }

      "returning a new Behaviour with the current rule when the Event has occurred after/on the time" in {
        val beh = new Behaviour(time => 5)
        val event = new Event[Int]
        val untilBeh = beh.until(1L, event, new Behaviour(time => 10))
        event.occur(new Occurrence(1L, 0))

        untilBeh.at(1) mustBe 10
      }

      "returning a Behaviour thats rule only 'changes' if the time is after the event" in {
        val event = new Event[Int]
        val beh = new Behaviour(time => 5).until(1L, event, new Behaviour(time => 10))
        event.occur(new Occurrence(2L, 5))

        beh.at(1) mustBe 5
      }
    }

    "provide a sample function" >> {
      "returning an Event that fires when the passed in event fires" in {
        val beh = new Behaviour(time => 5)
        val event = new Event[Int]

        val sampler = beh.sample(event)
        event.occur(new Occurrence(10, 5))

        sampler.occs().isEmpty mustBe false
      }

      "returning an Event that occurs with the current value of the Behaviour" in {
        val beh = new Behaviour(time => 5)
        val event = new Event[Int]
        var firedVal = 0

        val sampler = beh.sample(event)
        val occ = new Occurrence(10, 5)
        event.occur(occ)

        sampler.occs().last.time mustEqual occ.time
        sampler.occs().last.value mustEqual occ.value
      }
    }

    "provide a map function" >> {
      "returning a new Behaviour thats rule is func(this.at(now))" in {
        val beh = new Behaviour(time => 5)
        val func: Int => String = {
          int => int.toString
        }

        beh.map(func).at(0) mustEqual "5"
      }
    }
  }
}