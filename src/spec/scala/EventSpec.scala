import org.specs._
import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.Occurence
import com.github.oetzi.echo.Echo._

object EventSpec extends Specification {
	"Event" should {
		"be created without paramaters from the constructor" in {
			val event = new Event[Int]
			event.isInstanceOf[Event[Int]] mustBe true
		}
		
		"be created from Event.apply" in {
			val event = Event[Int]
			event.isInstanceOf[Event[Int]] mustBe true
		}
		
		"create a constant event when passed a time and value to Event.apply" >> {
			"that has one occurence" in {
				val event = Event(0, 5)
				
				event.occs.length mustBe 1
			}
			
			"that's occurence has the same paramaters as those passed" in {
				val event = Event(0, 5)
				
				event.occs.first.value mustBe 5
				event.occs.first.time mustBe 0.asInstanceOf[Time]
			}
		}
		
		"provide an occs function" >> {
			"returning an empty list for new Events" in {
				val event = new Event[Int]

				event.occs.isEmpty mustBe true
			}

			"returning a List with the correct length" in {
				val event = new Event[Int]
				event.occur(new Occurence(now, 5))
				event.occur(new Occurence(now, 5))

				event.occs.length mustBe 2
			}

			"returning a list with the correct values" in {
				val event = new Event[Int]
				val occurence = new Occurence(now, 5)
				event.occur(occurence)

				event.occs()(0) mustBe occurence
			}
		}
		
		"provide an occAt function (with paramater)" in {
			"returning a None for empty events" in {
				val event = new Event[Int]
				
				event.occAt(0) mustBe None
			}
			
			"returning an Occurence if the time has a match" in {
				val event = new Event[Int]
				val occ = new Occurence(5, 10)
				event.occur(occ)
				
				event.occAt(5).getOrElse(()) mustBe occ
			}
			
			"returning the rightmost Occurence if time has muliple matches" in {
				val event = new Event[Int]
				val occ = new Occurence(5, 9)
				val occ1 = new Occurence(5, 10)
				event.occur(occ)
				event.occur(occ1)
				
				event.occAt(5).getOrElse(()) mustBe occ1
			}
			
			"returning None if the time is before the first event" in {
				val event = new Event[Int]
				val occ = new Occurence(5, 9)
				event.occur(occ)
				
				event.occAt(4) mustBe None
			}
			
			"returning None if the time is after the last event" in {
				val event = new Event[Int]
				val occ = new Occurence(5, 9)
				event.occur(occ)
				
				event.occAt(6) mustBe None
			}
		}
		
		"provide a occsBefore function" in {
			"returning an empty list for a new Event" in {
				val event = new Event[Int]
				
				event.occsBefore(now).isEmpty mustBe true
			}
			
			"returning an empty list if the time is before the first occurence" in {
				val event = new Event[Int]
				event.occur(new Occurence(5, 10))
				
				event.occsBefore(4).isEmpty mustBe true
			}
			
			"returning an empty list if the time is equal to the first occurence" in {
				val event = new Event[Int]
				event.occur(new Occurence(5, 10))
				
				event.occsBefore(5).isEmpty mustBe true
			}
			
			"returning all the occurences if the time is after the last event" in {
				val event = new Event[Int]
				event.occur(new Occurence(5, 10))
				
				event.occsBefore(6).length mustBe 1
			}
			
			"returning the occurences before a certain event if first < time >= last" in {
				val event = new Event[Int]
				val matcher = new Occurence(5, 10)
				event.occur(matcher)
				event.occur(new Occurence(6, 10))
				
				event.occsBefore(6).last mustBe matcher
			}
		}

		"provide an occur function" >> {
			"that increase the length of occs" in {
				val event = new Event[Int]
				val length = event.occs.length
				event.occur(new Occurence(now, 5))

				event.occs.length mustBe length + 1
			}

			"that maintains order on added occurences" in {
				val event = new Event[Int]
				event.occur(new Occurence(15, 5))
				event.occur(new Occurence(10, 5))

				event.occs.last.time mustBe 15L
			}
		}

		"provide a foreach function" >> {
			"that creates a function that is executed on each future occurence" in {
				val event = new Event[Int]
				var fired = false

				event.foreach { occurence =>
					fired = true
				}

				event.occur(new Occurence(0, 10))

				fired mustBe true
			}

			"that creates a function that is executed on each past occurence" in {
				val event = new Event[Int]
				var fired = false
				event.occur(new Occurence(0, 10))

				event.foreach { occurence =>
					fired = true
				}

				fired mustBe true
			}
		}
		
		"provide a filter function" >> {
			"returning an event that contains the matching occurences of the original" in {
				val event = new Event[Int]
				event.occur(new Occurence(now, 4))
				event.occur(new Occurence(now, 5))
				event.occur(new Occurence(now, 6))
				
				event.filter(occ => occ.value > 5).occs.length mustBe 1
			}
			
			"returning an event that contains new events from the original" in {
				val event = new Event[Int]
				val newEvent = event.filter(occ => occ.value > 5)
				event.occur(new Occurence(now, 6))
				
				newEvent.occs.length mustBe 1
			}
			
			"returning an event that only matching contains new events from the original" in {
				val event = new Event[Int]
				val newEvent = event.filter(occ => occ.value > 5)
				event.occur(new Occurence(now, 6))
				event.occur(new Occurence(now, 5))
				
				newEvent.occs.length mustBe 1
			}
		}

		"provide a merge function" >> {
			"returning an Event that contains all the occurences of each" in {
				val eventOne = new Event[Int]
				val eventTwo = new Event[Int]
				val occurOne = new Occurence(now, 5)
				val occurTwo = new Occurence(now, 5)
				eventOne.occur(occurOne)
				eventTwo.occur(occurTwo)
				var matcher = List(occurOne, occurTwo)

				eventOne.merge(eventTwo).occs.foreach { occurence => 
					if (matcher.contains(occurence)) {
						matcher = matcher -- List(occurence)
					}
				}

				matcher.isEmpty mustBe true
			}

			"returning an Event with ordered occurences of each" in {
				val eventOne = new Event[Int]
				val eventTwo = new Event[Int]
				val occurOne = new Occurence(10, 5)
				val occurTwo = new Occurence(15, 5)
				eventOne.occur(occurOne)
				eventTwo.occur(occurTwo)
				var last : Occurence[Int] = new Occurence(0, 0)

				eventTwo.merge(eventOne).occs.foreach { occurence => 
					last.time must be_<=(occurence.time)
					last = occurence	
				}
			}

			"returning an Event that updates when the original updates" in {
				val eventOne = new Event[Int]
				val eventTwo = new Event[Int]

				val combEvent = eventOne.merge(eventTwo)
				eventOne.occur(new Occurence(now, 5))

				combEvent.occs.length mustBe 1
			}

			"returning an Event that updates when the paramater updates" in {
				val eventOne = new Event[Int]
				val eventTwo = new Event[Int]

				val combEvent = eventOne.merge(eventTwo)
				eventTwo.occur(new Occurence(now, 5))

				combEvent.occs.length mustBe 1
			}
		}
	}
}