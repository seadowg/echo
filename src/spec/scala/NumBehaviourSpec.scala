import org.specs._
import com.github.oetzi.echo.util.NumBehaviour
import com.github.oetzi.echo.core.Behaviour

object NumBehaviourSpec extends Specification {
	"+' function" should {
		"create a new Behaviour of type T from 'Behaviour[T] + Behaviour[T]'" in {
			val beh1 = new NumBehaviour(new Behaviour(time => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 + beh2).isInstanceOf[Behaviour[Int]] mustBe true
		}
		
		"create a new Behaviour with a combined rule" in {
			val beh1 = new NumBehaviour(new Behaviour(time => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 + beh2).now mustBe 10
		}
		
		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new NumBehaviour(new Behaviour(time => time))
			val beh2 = new Behaviour(time => time)
			val beh = beh1 + beh2
			
			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now
			
			first_val must_!= second_val
		}
	}
	
	"'-' function" should {
		"create a new Behaviour of type T from 'Behaviour[T] - Behaviour[T]'" in {
			val beh1 = new NumBehaviour(new Behaviour(time => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 - beh2).isInstanceOf[Behaviour[Int]] mustBe true
		}
		
		"create a new Behaviour with a combined rule" in {
			val beh1 = new NumBehaviour(new Behaviour(time => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 - beh2).now mustBe 0
		}
		
		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new NumBehaviour(new Behaviour(time => time))
			val beh2 = new Behaviour(time => 5.0)
			val beh = beh1 - beh2
			
			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now
			
			first_val must_!= second_val
		}
	}
	
	"'*' function" should {
		"create a new Behaviour of type T from 'Behaviour[T] * Behaviour[T]'" in {
			val beh1 = new NumBehaviour(new Behaviour(time => 5))
			val beh2 = new Behaviour(time => 5)

			(beh1 * beh2).isInstanceOf[Behaviour[Int]] mustBe true
		}

		"create a new Behaviour with a combined rule" in {
			val beh1 = new NumBehaviour(new Behaviour(time => 5))
			val beh2 = new Behaviour(time => 5)

			(beh1 * beh2).now mustBe 25
		}

		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new NumBehaviour(new Behaviour(time => time))
			val beh2 = new Behaviour(time => time)
			val beh = beh1 * beh2

			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now

			first_val must_!= second_val
		}
	}
}