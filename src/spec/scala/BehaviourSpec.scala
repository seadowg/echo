import org.specs._
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.Occurence

object BehaviourSpec extends Specification {
	"Behaviour" should {
		"create a new instance given a valid block" in {
			val beh = new Behaviour(time => time)
			beh must_!= null
		}
		
		"return Behaviour.now.toString() when toString() is called" in {
			val beh = new Behaviour(time => 5)
			beh.toString mustEqual "5"
		}
	}
	
	"'Behaviour.now' function" should {
		"should return the result of rule()" in {
			val beh = new Behaviour(time => (5 + 6))
			beh.now mustBe 11
		}
	}
	
	"'Behaviour.at' function" should {
		"should return the result of the rule with the passed value" in {
			val beh = new Behaviour(time => time * 5)
			beh.at(5).asInstanceOf[Int] mustBe 25
		}
	}
	
	"Behaviour.until" should {
		"return a new Behaviour" in {
			val beh = new Behaviour(time => 5)
			
			beh.until(new Event[Int], time => 5) must_!= beh
		}
		
		"return a new Behaviour with the current rule when the Event hasn't occured" in {
			val beh = new Behaviour(time => 5)
			
			beh.until(new Event[Int], time => 10).now mustBe 5
		}
		
		"return a new Behaviour with the new rule after the Event occurs" in {
			var beh = new Behaviour(time => 5)
			val event = new Event[Int]
			beh = beh.until(event, time => 10)
			event.occur(new Occurence(System.currentTimeMillis, 5))
			
			beh.now mustBe 10
		}
	}
	
	"'Behaviour.sample' function" should {
		"return a new Event when passed an Event of any type" in {
			val beh = new Behaviour(time => 5)
			val event = new Event[Unit]
			
			beh.sample(event).isInstanceOf[Event[Int]] mustBe true
		}
		
		"return an Event that fires when the passed in event fires" in {
			val beh = new Behaviour(time => 5)
			val event = new Event[Int]
			
			val sampler = beh.sample(event)
			event.occur(new Occurence(10, 5))
			
			sampler.occs.isEmpty mustBe false
		}
		
		"return an Event that occurs with the current value of the Behaviour" in {
			val beh = new Behaviour(time => 5)
			val event = new Event[Int]
			var firedVal = 0
			
			val sampler = beh.sample(event)
			val occ = new Occurence(10, 5)
			event.occur(occ)
			
			sampler.occs.last.time mustBe occ.time
			sampler.occs.last.value mustBe occ.value
		}
	}
	
	"'Behaviour.map' function" should {
		"return a new Behaviour of type B (for map(func : T => B))" in {
			val beh = new Behaviour(time => 5)
			val func : Int => String = { int => int.toString }
			
			beh.map(func).isInstanceOf[Behaviour[String]] mustBe true
		}
	}
}