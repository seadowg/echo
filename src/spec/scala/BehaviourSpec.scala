import org.specs._
import com.github.oetzi.echo.Behaviour
import com.github.oetzi.echo.Event

object BehaviourSpec extends Specification {
	"Behaviour" should {
		"create a new instance given a valid block" in {
			val beh = new Behaviour(time => time)
			beh must_!= null
		}
	}
	
	"'Behaviour.now' function" should {
		"should return the result of rule()" in {
			val beh = new Behaviour(time => (5 + 6))
			beh.now mustBe 11
		}
	}
	
	"'Behaviour.+' function" should {
		"create a new Behaviour of type T from 'Behaviour[T] + Behaviour[T]'" in {
			val beh1 = new Behaviour(time => 5)
			val beh2 = new Behaviour(time => 5)
			
			(beh1 + beh2).isInstanceOf[Behaviour[Int]] mustBe true
		}
		
		"create a new Behaviour with a combined rule" in {
			val beh1 = new Behaviour(time => 5)
			val beh2 = new Behaviour(time => 5)
			
			(beh1 + beh2).now mustBe 10
		}
		
		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new Behaviour(time => time)
			val beh2 = new Behaviour(time => time)
			val beh = beh1 + beh2
			
			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now
			
			first_val must_!= second_val
		}
	}
	
	"'Behaviour.-' function" should {
		"create a new Behaviour of type T from 'Behaviour[T] - Behaviour[T]'" in {
			val beh1 = new Behaviour(time => 5)
			val beh2 = new Behaviour(time => 5)
			
			(beh1 - beh2).isInstanceOf[Behaviour[Int]] mustBe true
		}
		
		"create a new Behaviour with a combined rule" in {
			val beh1 = new Behaviour(time => 5)
			val beh2 = new Behaviour(time => 5)
			
			(beh1 - beh2).now mustBe 0
		}
		
		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new Behaviour(time => time)
			val beh2 = new Behaviour(time => 5.0)
			val beh = beh1 - beh2
			
			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now
			
			first_val must_!= second_val
		}
	}
	
	"'Behaviour.*' function" should {
		"create a new Behaviour of type T from 'Behaviour[T] * Behaviour[T]'" in {
			val beh1 = new Behaviour(time => 5)
			val beh2 = new Behaviour(time => 5)

			(beh1 * beh2).isInstanceOf[Behaviour[Int]] mustBe true
		}

		"create a new Behaviour with a combined rule" in {
			val beh1 = new Behaviour(time => 5)
			val beh2 = new Behaviour(time => 5)

			(beh1 * beh2).now mustBe 25
		}

		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new Behaviour(time => time)
			val beh2 = new Behaviour(time => time)
			val beh = beh1 * beh2

			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now

			first_val must_!= second_val
		}
	}
	
	"'Behaviour.until' function" should {
		"create a new Behaviour of type T" in {
			val beh = new Behaviour(time => time)
			val event = new Event[Boolean]
			
			beh.until(event, time => 5.0).isInstanceOf[Behaviour[Double]] mustBe true
		}
		
		"create a Behaviour that is unchanged before the Event occurs" in {
			val beh = new Behaviour(time => 5)
			val event = new Event[Boolean]
			
			beh.until(event, time => 10).now mustBe 5
		}
		
		"create a Behaviour that is changed when the Event DOES occurs" in {
			val beh = new Behaviour(time => 5)
			val event = new Event[Boolean]
			
			val until_beh = beh.until(event, time => 10)
			event.occur(true)
			
			until_beh.now mustBe 10
		}
	} 
}