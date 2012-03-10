package com.github.oetzi.echo.test

import help.TestEvent
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._

import org.specs._

object EventSpec extends Specification {
	
	devMode()
	
  "EventSource" should {
    "be empty initially" in {
      val event = new TestEvent[Unit]
      
      event.top(now) mustEqual None
    }

    "increase the num on each occurrence with each occurrence" in {
      val event = new TestEvent[Unit]
      
      event.pubOccur(now, ())
      event.top(now).get.num mustEqual 1

      event.pubOccur(now, ())
      event.top(now).get.num mustEqual 2
    }
    
    "occur events normally if their time <= now" in {
      val event = new TestEvent[Unit]
      
      freezeTime(0) {
        () =>
          event.pubOccur(0, ())
          event.top(now).get.time
      }.mustEqual(0)

      freezeTime(0) {
        () =>
          event.pubOccur(1, ())
          event.top(1).get.time
      }.mustEqual(1)
    }
    
    "delay event occurrences if their time < now" in {
      val event = new TestEvent[Unit]
      
      freezeTime(1) {
        () =>
          event.pubOccur(0, ())
      }
      
      event.top(now).get.time mustEqual 1
    }
    
    "have an event that is kept up to date" in {
      val event = new TestEvent[Unit]
      val test = event.event
      event.pubOccur(now, ())
      
      test.top(now).get.num mustEqual 1
    }
    
    "have a map function" >> {
      "returning a mapped version of the Event" in {
          val event = new TestEvent[Int]
          event.pubOccur(now, 1)
  
          event.map(v => v + 1).top(now).get.value mustEqual 2
      }
      
      "that is kept to date with the original" in {
        val event = new TestEvent[Int]
        val map = event.map(v => v + 1)
        event.pubOccur(now, 1)

        map.top(now).get.value mustEqual 2
      }
    }

    "have a merge function" >> {
      "that returns an Event that is empty for empty sources" in {
        val event = new TestEvent[Unit].merge(new TestEvent[Unit])

        event.top(now) mustEqual None
      }
      
      "that uses the right source as its present if the left hasn't occurred" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]
        
        right.pubOccur(now, 5)
        
        left.merge(right).top(now).get.value mustEqual 5
        left.merge(right).top(now).get.num mustEqual 1
      }

      "that uses the left source as its present if the right hasn't occurred" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]

        left.pubOccur(now, 5)

        left.merge(right).top(now).get.value mustEqual 5
        left.merge(right).top(now).get.num mustEqual 1
      }

      "that uses the newest occurrence (in the present) if both source have occurred" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]

        freezeTime(0) {
          () =>
            left.pubOccur(0, 5)
            right.pubOccur(1, 6)
        }

        left.merge(right).top(now).get.value mustEqual 6
        left.merge(right).top(now).get.num mustEqual 2

        freezeTime(2) {
          () =>
            left.pubOccur(2, 4)
            right.pubOccur(4, 7)
        }

        val tuple = freezeTime(3) {
          () =>
            (left.merge(right).top(now).get.value,
            left.merge(right).top(now).get.num)
        }
        
        tuple mustEqual (4, 3)
      }
      
      "it uses the right occurrence if both source occurrences have equal time" in {
        val left = new TestEvent[Int]
        val right = new TestEvent[Int]

        freezeTime(0) {
          () =>
            left.pubOccur(0, 5)
            right.pubOccur(0, 6)
        }
        
        left.merge(right).top(now).get.value mustEqual 6
      }
    }
  }
}