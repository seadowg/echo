package com.github.oetzi.echo.test

import help.TestEvent
import com.github.oetzi.echo.Echo._

import org.specs._

object EventSpec extends Specification {
  "EventSource" should {
    "be empty initially" in {
      val event = new TestEvent[Unit]
      
      event.pubOccs().length mustEqual 0
    }
    
    "be appended to when occur is called" in {
      val event = new TestEvent[Unit]
      event.pubOccur(0, ())
      
      event.pubOccs().length mustEqual 1
    }
    
    "delay added occurrences if time is less than now" in {
      val event = new TestEvent[Unit]

      freezeTime(5) {
        () =>
          event.pubOccur(4, ())
      }

      event.pubOccs().last.time mustEqual 5
    }

    "should not change time for future occurrences" in {
      val event = new TestEvent[Unit]

      freezeTime(5) {
        () =>
          event.pubOccur(6, ())
      }

      event.pubOccs().last.time mustEqual 6
    }

    "have an 'event' function" >> {
      "returning a different Event (EventView) with the same occurrences" in {
        val event = new TestEvent[Unit]
        event.pubOccur(now, ())
        val eventView = event.event

        eventView.lengthAt(now) mustEqual 1


        "returning an event that is always up to date with the original" in {
          val event = new TestEvent[Unit]
          val eventView = event.event
          event.pubOccur(now, ())

          eventView.lengthAt(now) mustEqual 1
        }
      }
    }

    "have a 'map' function" >> {
      "returning a new Event with with mapped values" in {
        val event = new TestEvent[Int]
        event.pubOccur(now, 0)

        event.map(v => v + 1).lastValueAt(now).getOrElse(-1) mustEqual 1
      }
      
      "returing a new Event that maps new occurrences" in {
        val event = new TestEvent[Int]
        val map = event.map(v => v + 1)
        event.pubOccur(now, 0)  
        
        map.lastValueAt(now).getOrElse(-1) mustEqual 1
      }
    }
    
    "have a 'filter' function" >> {
      "returning a new Event with only occurrences that pass the predicate" in {
        val event = new TestEvent[Int]
        event.pubOccur(now, 0)
        event.pubOccur(now, 1)

        event.filter(occ => occ.value < 1).lastValueAt(now).getOrElse(-1) mustEqual 0
      }

      "returning a new Event that filters new occurrences" in {
        val event = new TestEvent[Int]
        val map = event.filter(o => o.value == 0)
        event.pubOccur(now, 0)
        event.pubOccur(now, 1)

        map.lastValueAt(now).getOrElse(-1) mustEqual 0
      }
    }
  }  
}