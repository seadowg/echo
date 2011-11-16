import org.specs._
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.Event

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
	
	"'Behaviour.sample' function" should {
		"return a new Event when passed an Event of any type" in {
			val beh = new Behaviour(time => 5)
			val event = new Event[Unit]
			
			beh.sample(event).isInstanceOf[Event[Int]] mustBe true
		}
		
		"return an Event that fires when the passed in event fires" in {
			val beh = new Behaviour(time => 5)
			val event = new Event[Unit]
			var fired = false
			
			beh.sample(event).foreach(event => fired = true)
			event.occur()
			
			fired mustBe true
		}
		
		"return an Event that occurs with the current value of the Behaviour" in {
			val beh = new Behaviour(time => 5)
			val event = new Event[Unit]
			var firedVal = 0
			
			beh.sample(event).foreach(event => firedVal = event)
			event.occur()
			
			firedVal mustBe 5
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