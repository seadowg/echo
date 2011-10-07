import org.specs._
import com.github.oetzi.echo.Event
import java.util.Date

object EventSpec extends Specification {
	"Event" should {
		"be created without paramaters" in {
			val event = new Event[Int]
			event.isInstanceOf[Event[Int]] mustBe true
		}
	}
	
	"Event.each function" should {
		"work" in {
			val event = new Event[Int]
			event.each(event => event)
			true mustBe true
		}
	}
	
	"Event.occur function" should {
		"executes assigned 'edges'" in {
			val event = new Event[Int]
			var x = 5
			
			event.each(event => x = event)
			event.occur(10)
			x mustBe 10
		}
		
		"executes edges in the corret order" in {
			val event = new Event[Int]
			var first : Double = 0
			var second : Double = 0
			
			event.each({
				event => first = new Date().getTime;
				Thread.sleep(1);
			})
			event.each(event => second = new Date().getTime)
			event.occur(5)
			
			first < second mustBe true
		}
		
		"passes the correct value for the event" in {
			val event = new Event[Int]
			var x = 5
			
			event.each(event => x = event)
			event.occur(10)
			x mustBe 10
		}
	}
	
	"Event.++ function" should {
		"return a new event with the same type" in {
			val event1 = new Event[Int]
			val event2 = new Event[Int]
			val comp_event = event1 ++ event2
			
			comp_event.isInstanceOf[Event[Int]] mustBe true
		}
		
		"create an event that will fire when the caller fires (with the correct value)" in {
			val event1 = new Event[Int]
			val event2 = new Event[Int]
			val comp_event = event1 ++ event2
			var fired = 0
			
			comp_event.each(event => fired = event)
			event1.occur(5)
			
			fired mustBe 5
		}
		
		"create an event that will fire when the paramater fires (with the correct value)" in {
			val event1 = new Event[Int]
			val event2 = new Event[Int]
			val comp_event = event1 ++ event2
			var fired = 0
			
			comp_event.each(event => fired = event)
			event2.occur(6)
			
			fired mustBe 6
		}
	}
	
	"Event.filter function" should {
		"return a new event with the same type" in {
			val event = new Event[Int]
			event.filter(_ == 0).isInstanceOf[Event[Int]] mustBe true
		}
		
		"create an event that fires if predicate is true for the creating instance" in {
			val event = new Event[Int]
			var fired = false
			event.filter(_ == 0).each(event => fired = true)
			event.occur(0)
			
			fired mustBe true
		}
		
		"create an event that doesn't fire if the predicate is false for the creating instance" in {
			val event = new Event[Int]
			var fired = false
			event.filter(_ == 0).each(event => fired = true)
			event.occur(1)
			
			fired mustBe false
		}
		
		"create an event that recieves the same value as the original" in {
			val event = new Event[Int]
			var fired = 0
			event.filter(_ == 6).each(event => fired = event)
			event.occur(6)
			
			fired mustBe 6
		}
	}
}