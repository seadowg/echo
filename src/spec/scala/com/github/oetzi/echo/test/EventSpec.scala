package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.Occurrence
import com.github.oetzi.echo.Echo._

object EventSpec extends Specification {
  "Event" should {
    "be created without paramaters from the constructor" in {
      val event = new Event[Int]
      event.isInstanceOf[Event[Int]] mustBe true
    }

    "be created from Event.apply" in {
      val event = Event[Int]()
      event.isInstanceOf[Event[Int]] mustBe true
    }

    "provide a join operation" >> {
      "that returns a flattened empty event for an empty event" in {
        val event = Event[Event[Int]]()

        Event.join(event).isInstanceOf[Event[Int]] mustBe true
      }

      "that returns a flattened event with the occurences of each occurence" in {
        val event = Event[Event[Int]]()
        var list = List[Occurrence[Int]]()

        for (i <- 0 until 3) {
          val e = Event[Int]()
          val o = new Occurrence(i, i)
          e.occur(o)
          list = list ++ List(o)
          event.occur(new Occurrence(i, e))
        }

        val occs = Event.join(event).occs()

        for (i <- 0 until occs.length) {
          occs(i).time mustEqual list(i).time
          occs(i).value mustEqual list(i).value
        }
      }

      "that returns a flattened event that is kept up to date with the original's occurence event's occurences" in {
        val event = Event[Event[Int]]()
        val occEvent = new Event[Int]
        event.occur(new Occurrence(5, occEvent))
        val joined = Event.join(event)

        occEvent.occur(new Occurrence(5, 5))

        joined.occs().length mustBe 1
      }

      "that returns a flattened event that is kept up to date with any new occurence events in the original" in {
        val event = Event[Event[Int]]()
        val occEvent = new Event[Int]
        val joined = Event.join(event)

        event.occur(new Occurrence(5, occEvent))
        occEvent.occur(new Occurrence(5, 5))

        joined.occs().length mustBe 1
      }

      "that delays any inner occurrences that happen before its event parent" in {
        val event = Event[Event[Int]]()
        val occEvent = new Event[Int]()

        occEvent.occur(new Occurrence(4, 10))
        event.occur(new Occurrence(5, occEvent))

        val joined = Event.join(event)

        joined.occs().head.time mustEqual 5
      }
    }

    "create a constant event when passed a time and value to Event.apply" >> {
      "that has one occurrence" in {
        val event = Event(0, 5)

        event.occs().length mustBe 1
      }

      "that's occurrence has the same paramaters as those passed" in {
        val event = Event(0, 5)

        event.occs().head.value mustEqual 5
        event.occs().head.time mustEqual 0.asInstanceOf[Time]
      }
    }

    "provide an occs function" >> {
      "returning an empty list for new Events" in {
        val event = new Event[Int]

        event.occs().isEmpty mustBe true
      }

      "returning a List with the correct length" in {
        val event = new Event[Int]
        event.occur(new Occurrence(0, 5))
        event.occur(new Occurrence(0, 5))

        event.occs().length mustBe 2
      }

      "returning a list with the correct values" in {
        val event = new Event[Int]
        val occurrence = new Occurrence(0, 5)
        event.occur(occurrence)

        event.occs()(0) mustBe occurrence
      }
    }

    "provide an occur function" >> {
      "that increase the length of occs" in {
        val event = new Event[Int]
        val length = event.occs().length
        event.occur(new Occurrence(0, 5))

        event.occs().length mustBe length + 1
      }

      "that maintains order on added occurences" in {
        val event = new Event[Int]
        event.occur(new Occurrence(15, 5))
        event.occur(new Occurrence(10, 5))
        event.occur(new Occurrence(14, 5))
        event.occur(new Occurrence(16, 5))

        event.occs()(0).time mustEqual 10
        event.occs()(1).time mustEqual 14
        event.occs()(2).time mustEqual 15
        event.occs()(3).time mustEqual 16
      }
    }

    "provide a map function" >> {
      "returning an event that returns the the same number of occurences" in {
        val event = new Event[Int]
        event.occur(new Occurrence(0, 5))

        event.map(occ => occ).occs().length mustBe 1
      }

      "returning an event that contains occurences that have been mapped correctly" in {
        val event = new Event[Int]
        event.occur(new Occurrence(0, 5))

        event.map(occ => new Occurrence(occ.time, occ.value.toString)).occs().head.value mustEqual "5"
      }

      "returning an event that contains new occurences from the original" in {
        val event = new Event[Int]
        val newEvent = event.map(occ => occ)
        event.occur(new Occurrence(0, 5))

        newEvent.occs().length mustBe 1
      }
    }

    "provide a mapV function" >> {
      "returning an event that returns the the same number of occurences" in {
        val event = new Event[Int]
        event.occur(new Occurrence(0, 5))

        event.mapV(occ => occ).occs().length mustBe 1
      }

      "returning an event that contains occurences that have been mapped correctly" in {
        val event = new Event[Int]
        event.occur(new Occurrence(0, 5))

        event.mapV(occ => occ.toString).occs().head.value mustEqual "5"
      }

      "returning an event that contains new occurences from the original" in {
        val event = new Event[Int]
        val newEvent = event.mapV(occ => occ)
        event.occur(new Occurrence(0, 5))

        newEvent.occs().length mustBe 1
      }
    }

    "provide a filter function" >> {
      "returning an event that contains the matching occurences of the original" in {
        val event = new Event[Int]
        event.occur(new Occurrence(0, 4))
        event.occur(new Occurrence(0, 5))
        event.occur(new Occurrence(0, 6))

        event.filter(occ => occ.value > 5).occs().length mustBe 1
      }

      "returning an event that contains new events from the original" in {
        val event = new Event[Int]
        val newEvent = event.filter(occ => occ.value > 5)
        event.occur(new Occurrence(0, 6))

        newEvent.occs().length mustBe 1
      }

      "returning an event that only matching contains new events from the original" in {
        val event = new Event[Int]
        val newEvent = event.filter(occ => occ.value > 5)
        event.occur(new Occurrence(0, 6))
        event.occur(new Occurrence(0, 5))

        newEvent.occs().length mustBe 1
      }
    }

    "provide a merge function" >> {
      "returning an Event that contains all the occurences of each" in {
        val eventOne = new Event[Int]
        val eventTwo = new Event[Int]
        val occurOne = new Occurrence(0, 5)
        val occurTwo = new Occurrence(0, 5)
        eventOne.occur(occurOne)
        eventTwo.occur(occurTwo)
        var matcher = List(occurOne, occurTwo)

        eventOne.merge(eventTwo).occs().foreach {
          occurrence =>
            if (matcher.contains(occurrence)) {
              matcher = matcher.filterNot(occ => occurrence == occ)
            }
        }

        matcher.isEmpty mustBe true
      }

      "returning an Event with ordered occurences of each" in {
        val eventOne = new Event[Int]
        val eventTwo = new Event[Int]
        val occurOne = new Occurrence(10, 5)
        val occurTwo = new Occurrence(15, 5)
        eventOne.occur(occurOne)
        eventTwo.occur(occurTwo)
        var last: Occurrence[Int] = new Occurrence(0, 0)

        eventTwo.merge(eventOne).occs().foreach {
          occurrence =>
            last.time must be_<=(occurrence.time)
            last = occurrence
        }
      }

      "returning an Event that updates when the original updates" in {
        val eventOne = new Event[Int]
        val eventTwo = new Event[Int]

        val combEvent = eventOne.merge(eventTwo)
        eventOne.occur(new Occurrence(0, 5))

        combEvent.occs().length mustBe 1
      }

      "returning an Event that updates when the paramater updates" in {
        val eventOne = new Event[Int]
        val eventTwo = new Event[Int]

        val combEvent = eventOne.merge(eventTwo)
        eventTwo.occur(new Occurrence(0, 5))

        combEvent.occs().length mustBe 1
      }
    }

    "provide a lastIndexAt function" in {
      "that returns None for an empty Event" in {
        val event = new Event[Unit]

        event.lastIndexAt(0) mustBe None
      }

      "that retunrns the last index for a filled Event" in {
        val event = new Event[Int]
        event.occur(new Occurrence(0, 1))
        event.occur(new Occurrence(1, 2))
        event.occur(new Occurrence(2, 3))

        event.lastIndexAt(1) mustEqual Some(1)
        event.lastIndexAt(3) mustEqual Some(2)
        event.lastIndexAt(0) mustEqual Some(0)
      }
    }

    "provide a lastValueAt function" in {
      "that returns None for an empty Event" in {
        val event = new Event[Unit]

        event.lastValueAt(0) mustBe None
      }

      "that retunrns the last occurrence value for a filled Event" in {
        val event = new Event[Int]
        event.occur(new Occurrence(0, 1))
        event.occur(new Occurrence(1, 2))
        event.occur(new Occurrence(2, 3))

        event.lastValueAt(1) mustEqual Some(2)
        event.lastValueAt(3) mustEqual Some(3)
        event.lastValueAt(0) mustEqual Some(1)
      }
    }
  }
}