package com.github.oetzi.echo.test

import help.TestEvent
import org.specs._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.core.Behavior

object BehaviorSpec extends Specification {

  devMode()

  "Behavior" should {
    "create a new instance given a valid block" in {
      val beh = new Behavior(time => time)
      beh must_!= null
    }

    "be created from from Behavior.apply" in {
      val behaviour = Behavior(time => 5)
      behaviour.isInstanceOf[Behavior[Int]] mustBe true
    }

    "provide an until(non-relative) function" >> {
      "returning a new Behavior" in {
        val beh = new Behavior(time => 5)

        beh.until(new TestEvent[Int], new Behavior(time => 5)) must_!= beh
      }

      "returning a new Behavior with the current rule when the Event hasn't occured" in {
        val beh = new Behavior(time => 5)

        beh.until(new TestEvent[Int], new Behavior(time => 10)).eval() mustBe 5
      }

      "returning a new Behavior with the new rule after the Event occurs" in {
        var beh = new Behavior(time => 5)
        val event = new TestEvent[Int]
        beh = beh.until(event, new Behavior(time => 10))

        freezeTime(1) {
          () =>
            event.pubOccur(5)
            beh.eval()
        }.mustEqual(10)
      }
    }

    "provide an until(relative) function" >> {
      "returning a new Behavior" in {
        val beh = new Behavior(time => 5)

        beh.until(0L, new TestEvent[Int], new Behavior(time => 5)) must_!= beh
      }

      "returning a new Behavior with the current rule when the Event hasn't occured" in {
        val beh = new Behavior(time => 5)

        freezeTime(1) {
          () =>
            beh.until(0L, new TestEvent[Int], new Behavior(time => 10)).eval()
        }.mustEqual(5)
      }

      "returning a new Behavior with the current rule when the Event has occurred before the time" in {
        val beh = new Behavior(time => 5)
        val event = new TestEvent[Int]

        freezeTime(0) {
          () =>
            event.pubOccur(0)
        }

        freezeTime(1) {
          () =>
            beh.until(1L, event, new Behavior(time => 10)).eval()
        }.mustBe(5)
      }

      "returning a new Behavior with the current rule when the Event has occurred after/on the time" in {
        val beh = new Behavior(time => 5)
        val event = new TestEvent[Int]
        val untilBeh = beh.until(1L, event, new Behavior(time => 10))

        freezeTime(1) {
          () =>
            event.pubOccur(0)
            untilBeh.eval()
        }.mustBe(10)
      }
    }

    "provide a map function" >> {
      "returning a new Behavior thats rule is func(this.at(t))" in {
        val beh = new Behavior(time => 5)
        val func: Int => String = {
          int => int.toString
        }

        freezeTime(1) {
          () =>
            beh.map(func).eval()
        }.mustEqual("5")
      }
    }

    "provide a combining map2 function" >> {
      "returning a new Behavior thats rule is func(this.at(t), beh.at(t))" in {
        val beh = new Behavior(time => time.toInt)
        val func: (Int, String) => String = {
          (int, string) => int.toString ++ string
        }

        freezeTime(5) {
          () =>
            beh.map2(new Behavior(time => time.toString))(func).eval()
        }.mustEqual("55.0")
      }
    }

    "provide a combining map3 function" >> {
      "returning a new Behavior thats rule is func(this.at(t), beh.at(t), beh.at(t))" in {
        val beh = new Behavior(time => time.toInt)
        val beh1 = new Behavior(time => time.toString)
        val func: (Int, String, String) => String = {
          (int, string, string1) => int.toString ++ string ++ string1
        }

        freezeTime(5) {
          () =>
            beh.map3(beh1, beh1)(func).eval()
        }.mustEqual("55.05.0")
      }
    }

    "provide a toggle function" >> {
      "returning a new Behavior that uses the original rule for an empty Event" in {
        val beh = new Behavior(time => 5)

        freezeTime(0) {
          () =>
            beh.toggle(new TestEvent[Unit], new Behavior(time => 10)).eval()
        }.mustBe(5)
      }

      "returning a new Behavior that uses the next rule when time >= only occurrence" in {
        val beh = new Behavior(time => 5)

        val value = freezeTime(0) {
          () =>
            val event = new TestEvent[Unit]
            event.pubOccur(())
            beh.toggle(event, new Behavior(time => 10)).eval()
        }

        value mustBe 10
      }

      "returning a new Behavior that uses the first rule when time >= second (only two)" in {
        val beh = new Behavior(time => 5)
        val event = new TestEvent[Unit]

        freezeTime(3) {
          () =>
            event.pubOccur(())
            event.pubOccur(())
            beh.toggle(event, new Behavior(time => 10)).eval()
        }.mustBe(5)
      }
    }

    "provide a sample function" >> {
      "returning a new Event that is empty for an empty source" in {
        val event = new TestEvent[Unit]
        val beh = new Behavior(time => time)

        beh.sample(event).top(now()) mustBe None
      }

      "returning an event that samples the Behaviour on occurrences" in {
        val event = new TestEvent[Unit]
        val beh = new Behavior(time => time)
        val sampler = beh.sample(event)

        freezeTime(1) {
          () =>
            event.pubOccur(())
        }

        sampler.top(now()).get.time mustEqual 1
        sampler.top(now()).get.value mustEqual 1
      }
    }
  }
}