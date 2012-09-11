package com.github.oetzi.echo.core.test

import help.TestEvent
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.core._

import org.specs._

object EventSpec extends Specification {

  devMode()

  "EventSource" should {
    "be empty initially" in {
      val event = new TestEvent[Unit]

      event.top() mustEqual None
    }

    "increase the num on each occurrence with each occurrence" in {
      val event = new TestEvent[Unit]

      event.pubOccur(())
      event.top().get.num mustEqual 1

      event.pubOccur(())
      event.top().get.num mustEqual 2
    }

    "have an event that is kept up to date" in {
      val event = new TestEvent[Unit]
      val test = event.event()
      event.pubOccur(())

      test.top().get.num mustEqual 1
    }

    "executes hooked blocks when it occurs" in {
      val event = new TestEvent[Int]
      var passed: Occurrence[Int] = null
      event.hook(occ => passed = occ)

      event.pubOccur(5)
      passed.value mustEqual 5
    }
    
    "has hook functionality" in {
      "that executes hooked functions on occur" in {
        val event = new TestEvent[Int]
        var ran = false
        event.hook(occ => ran = true)
        
        event.pubOccur(5)
        
        ran mustEqual true
      }
      
      "that executes hooked functions in frozen time" in {
        val event = new TestEvent[Int]
        var time = 0D
        event.hook {
          occ =>
            Thread.sleep(1)
            time = now()
        }
        
        event.pubOccur(5)
        
        time mustEqual event.top().get.time
      }
    }

    "have a map function" >> {
      "returning a mapped version of the Event" in {
        val event = new TestEvent[Int]
        event.pubOccur(1)

        event.map((t, v) => v + 1).top().get.value mustEqual 2
      }

      "that is kept to date with the original" in {
        val event = new TestEvent[Int]
        val map = event.map((t, v) => v + 1)
        event.pubOccur(1)

        map.top().get.value mustEqual 2
      }

      "that executes hooks with a mapped occurrence" in {
        val event = new TestEvent[Int]
        val map = event.map((t, v) => v * 5)
        var passed: Occurrence[Int] = null
        map.hook(occ => passed = occ)

        event.pubOccur(5)
        passed.value mustEqual 25
      }
    }

    "have a filter function" >> {
      "returning an empty Event for an empty Event" in {
        val event = new TestEvent[Int]

        event.filter(e => e == 5).top() mustEqual None
      }

      "returning an Event that only includes matching occurrences" in {
        val event = new TestEvent[Int]
        val filter = event.filter(e => e == 5)

        event.pubOccur(5)
        event.pubOccur(6)

        filter.top().get.value mustEqual 5
      }

      "it only executes hooks for correct occurrences" in {
        val event = new TestEvent[Int]
        val filter = event.filter(e => e == 5)
        var execed = 0

        filter.hook {
          occ => execed += 1
        }

        event.pubOccur(5)
        event.pubOccur(6)

        execed mustEqual 1
      }
    }


    "have a merge function" >> {
      "that returns an Event that is empty for empty sources" in {
        val event = new TestEvent[Unit].merge(new TestEvent[Unit])

        event.top() mustEqual None
      }

      "that uses the right source as its present if the left hasn't occurred" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]

        right.pubOccur(5)

        left.merge(right).top().get.value mustEqual 5
        left.merge(right).top().get.num mustEqual 1
      }

      "that uses the left source as its present if the right hasn't occurred" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]

        left.pubOccur(5)

        left.merge(right).top().get.value mustEqual 5
        left.merge(right).top().get.num mustEqual 1
      }

      "that uses the newest occurrence (in the present) if both source have occurred" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]

        freezeTime(0) {
          left.pubOccur(5)
        }
        freezeTime(1) {
          right.pubOccur(6)
        }

        left.merge(right).top().get.value mustEqual 6
        left.merge(right).top().get.num mustEqual 2

        freezeTime(2) {
          left.pubOccur(4)
        }

        val tuple = freezeTime(3) {
          (left.merge(right).top().get.value,
            left.merge(right).top().get.num)
        }

        tuple mustEqual(4, 3)
      }

      "it uses the right occurrence if both source occurrences have equal time" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]

        freezeTime(0) {
          left.pubOccur(5)
          right.pubOccur(6)
        }

        left.merge(right).top().get.value mustEqual 6
      }

      "that executes hooks when either event occurs" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]
        val merge = left.merge(right)

        var passed: Occurrence[Int] = null
        merge.hook(occ => passed = occ)

        left.pubOccur(5)
        passed.value mustEqual 5

        right.pubOccur(6)
        passed.value mustEqual 6
      }
    }

    "have an Event.join function that" >> {
      "returns an empty Event for empty Events" in {
        val event = Event.join(new TestEvent[Event[Int]])

        event.top() mustEqual None
      }

      "always has the newest occurrence from any occurred Event" in {
        val source = new TestEvent[Event[Int]]
        val event = Event.join(source)

        val occurSource1 = new TestEvent[Int]
        val occurSource2 = new TestEvent[Int]
        source.pubOccur(occurSource1)
        source.pubOccur(occurSource2)

        occurSource2.pubOccur(5)
        occurSource1.pubOccur(6)

        event.top().get.value mustEqual 6
      }

      "uses right precedence if two occurrences from seperate events are equal" in {
        val source = new TestEvent[Event[Int]]
        val event = Event.join(source)

        val occurSource1 = new TestEvent[Int]
        val occurSource2 = new TestEvent[Int]
        source.pubOccur(occurSource1)
        source.pubOccur(occurSource2)

        freezeTime(now()) {
          occurSource2.pubOccur(7)
          occurSource1.pubOccur(6)
        }

        event.top().get.value mustEqual 7
      }

      "it delays old occurrences in occuring Events" in {
        val source = new TestEvent[Event[Int]]
        val event = Event.join(source)

        val occurSource1 = new TestEvent[Int]

        freezeTime(1) {
          occurSource1.pubOccur(5)
        }

        freezeTime(2) {
          source.pubOccur(occurSource1)
        }

        event.top().get.time mustEqual 2
      }

      "it should have correct length" in {
        val source = new TestEvent[Event[Int]]
        val event = Event.join(source)

        val occurSource1 = new TestEvent[Int]
        val occurSource2 = new TestEvent[Int]

        freezeTime(1) {
          occurSource1.pubOccur(5)
        }

        source.pubOccur(occurSource1)
        source.pubOccur(occurSource2)
        occurSource2.pubOccur(1)
        occurSource1.pubOccur(2)

        event.top().get.num mustEqual 3
      }
    }
    
    "have a foldLeft function that" >> {
      "returns a Behaviour that has the initial value for an empty Event" in {
        val event = new TestEvent[Int]
        
        event.foldLeft(0) {
          (l, r) => l
        }.eval() mustEqual 0
      }
      
      "returns a Behaviour that applies the function across Event occurrences" in {
        val event = new TestEvent[Int]
        val folded = event.foldLeft(1) {
          (l, r) =>
            l + r
        }
        
        event.pubOccur(2)
        event.pubOccur(3)
        
        folded.eval() mustEqual 6
      }
    }
  }
}