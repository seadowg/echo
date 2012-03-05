package com.github.oetzi.echo.test

import help.TestEvent
import org.specs._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.{Event, Behaviour}

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

    "provide an until(non-relative) function" >> {
      "returning a new Behaviour" in {
        val beh = new Behaviour(time => 5)

        beh.until(new TestEvent[Int], new Behaviour(time => 5)) must_!= beh
      }

      "returning a new Behaviour with the current rule when the Event hasn't occured" in {
        val beh = new Behaviour(time => 5)

        beh.until(new TestEvent[Int], new Behaviour(time => 10)).eval() mustBe 5
      }

      "returning a new Behaviour with the new rule after the Event occurs" in {
        var beh = new Behaviour(time => 5)
        val event = new TestEvent[Int]
        beh = beh.until(event, new Behaviour(time => 10))
        
        freezeTime(0) { () =>
          event.pubOccur(1, 5)
        }
        
        freezeTime(1) {
          () =>
            beh.eval()
        }.mustEqual(10)
      }

      "returning a Behaviour thats rule only 'changes' if the time is after the event" in {
        val event = new TestEvent[Int]
        val beh = new Behaviour(time => 5).until(event, new Behaviour(time => 10))

        freezeTime(0) { () =>
          event.pubOccur(2, 5)
        }

        freezeTime(1) {
          () =>
            beh.eval()
        }.mustEqual(5)
      }
    }

    "provide an until(relative) function" >> {
      "returning a new Behaviour" in {
        val beh = new Behaviour(time => 5)

        beh.until(0L, new TestEvent[Int], new Behaviour(time => 5)) must_!= beh
      }

      "returning a new Behaviour with the current rule when the Event hasn't occured" in {
        val beh = new Behaviour(time => 5)

        freezeTime(1) {
          () =>
            beh.until(0L, new TestEvent[Int], new Behaviour(time => 10)).eval()
        }.mustEqual(5)
      }

      "returning a new Behaviour with the current rule when the Event has occurred before the time" in {
        val beh = new Behaviour(time => 5)
        val event = new TestEvent[Int]
        
        freezeTime(0) { () =>
          event.pubOccur(0L, 0)
        }

        freezeTime(1) {
          () =>
            beh.until(1L, event, new Behaviour(time => 10)).eval()
        }.mustBe(5)
      }

      "returning a new Behaviour with the current rule when the Event has occurred after/on the time" in {
        val beh = new Behaviour(time => 5)
        val event = new TestEvent[Int]
        val untilBeh = beh.until(1L, event, new Behaviour(time => 10))

        freezeTime(0) { () =>
          event.pubOccur(1L, 0)
        }

        freezeTime(1) {
          () =>
            untilBeh.eval()
        }.mustBe(10)
      }

      "returning a Behaviour thats rule only 'changes' if the time is after the event" in {
        val event = new TestEvent[Int]
        val beh = new Behaviour(time => 5).until(1L, event, new Behaviour(time => 10))

        freezeTime(0) { () =>
          event.pubOccur(2L, 5)
        }

        freezeTime(1) {
          () =>
            beh.eval()
        }.mustBe(5)
      }
    }

    "provide a map function" >> {
      "returning a new Behaviour thats rule is func(this.at(t))" in {
        val beh = new Behaviour(time => 5)
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
      "returning a new Behaviour thats rule is func(this.at(t), beh.at(t))" in {
        val beh = new Behaviour(time => time.toInt)
        val func: (Int, String) => String = {
          (int, string) => int.toString ++ string
        }

        freezeTime(5) {
          () =>
            beh.map2(new Behaviour(time => time.toString))(func).eval()
        }.mustEqual("55.0")
      }
    }

    "provide a combining map3 function" >> {
      "returning a new Behaviour thats rule is func(this.at(t), beh.at(t), beh.at(t))" in {
        val beh = new Behaviour(time => time.toInt)
        val beh1 = new Behaviour(time => time.toString)
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
      "returning a new Behaviour that uses the original rule for an empty Event" in {
        val beh = new Behaviour(time => 5)

        freezeTime(0) {
          () =>
            beh.toggle(new TestEvent[Unit], new Behaviour(time => 10)).eval()   
        }.mustBe(5)
      }
      
      "returning a new Behaviour that uses the original rule when time < first occurrence" in {
        val beh = new Behaviour(time => 5)

        val value = freezeTime(0) { () =>
          beh.toggle(Event(1, ()), new Behaviour(time => 10)).eval()
        }

        value mustBe 5
      }

      "returning a new Behaviour that uses the next rule when time >= only occurrence" in {
        val beh = new Behaviour(time => 5)

        val value = freezeTime(0) { () =>
          beh.toggle(Event(1, ()), new Behaviour(time => 10)).eval
        }

        value mustBe 5
      }
      
      "returning a new Behaviour that uses the next rule when time >= first but < second occurrence" in {
        val beh = new Behaviour(time => 5)
        val event = new TestEvent[Unit]
        
        freezeTime(0) { () =>
          event.pubOccur(1, ())
          event.pubOccur(3, ())  
        }

        freezeTime(2) {
          () =>
            beh.toggle(event, new Behaviour(time => 10)).eval()
        }.mustBe(10)
      }

      "returning a new Behaviour that uses the first rule when time >= second (only two)" in {
        val beh = new Behaviour(time => 5)
        val event = new TestEvent[Unit]
        
        freezeTime(0) { () =>
          event.pubOccur(1, ())
          event.pubOccur(3, ())
        }

        freezeTime(3) {
          () =>
            beh.toggle(event, new Behaviour(time => 10)).eval()
        }.mustBe(5)
      }
    }
  }
}