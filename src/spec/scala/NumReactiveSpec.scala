import org.specs._
import com.github.oetzi.echo.util.NumReactive
import com.github.oetzi.echo.core.Reactive
import com.github.oetzi.echo.core.EmbdContinuous
import com.github.oetzi.echo.core.Behaviour

object NumReactiveSpec extends Specification {
	"+' function" should {
		"create a new Behaviour of type T from 'Reactive[T] + Behaviour[T]'" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 + beh2).isInstanceOf[Reactive[Int]] mustBe true
		}
		
		"create a new Behaviour with a combined rule" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 + beh2).now mustBe 10
		}
		
		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new NumReactive(new Reactive(() => 5.0))
			val beh2 = new Behaviour(time => time)
			val beh = beh1 + beh2
			
			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now
			
			first_val must_!= second_val
		}
		
		"create a new Reactive of type T from 'Reactive[T] + Reactive[T]'" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Reactive(() => 5)
			
			(beh1 + beh2).isInstanceOf[Reactive[Int]] mustBe true
		}
		
		"create a new Reactive with a combined rule" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Reactive(() => 5)
			
			(beh1 + beh2).now mustBe 10
		}
		
		"create a Reactive that's rule is dynamic (not evaluated during addition)" in {
			var x = 0
			val beh1 = new NumReactive(new Reactive(() => x))
			val beh2 = new Reactive(() => 5)
			val beh = beh1 + beh2
			
			val first_val = beh.now
			x = 2
			val second_val = beh.now
			
			first_val must_!= second_val
		}
	}
	
	"'-' function" should {
		"create a new Behaviour of type T from 'Reactive[T] + Behaviour[T]'" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 - beh2).isInstanceOf[Reactive[Int]] mustBe true
		}
		
		"create a new Behaviour with a combined rule" in {
			val beh1 = new NumReactive(new Reactive((	) => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 - beh2).now mustBe 0
		}
		
		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new NumReactive(new Reactive(() => 5.0))
			val beh2 = new Behaviour(time =>time)
			val beh = beh1 - beh2
			
			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now
			
			first_val must_!= second_val
		}
		
		"create a new Reactive of type T from 'Reactive[T] + Reactive[T]'" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Reactive(() => 5)
			
			(beh1 - beh2).isInstanceOf[Reactive[Int]] mustBe true
		}
		
		"create a new Reactive with a combined rule" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Reactive(() => 5)
			
			(beh1 - beh2).now mustBe 0
		}
		
		"create a Reactive that's rule is dynamic (not evaluated during addition)" in {
			var x = 0
			val beh1 = new NumReactive(new Reactive(() => x))
			val beh2 = new Reactive(() => 5)
			val beh = beh1 - beh2
			
			val first_val = beh.now
			x = 2
			val second_val = beh.now
			
			first_val must_!= second_val
		}	
	}
	
	"'*' function" should {
		"create a new Behaviour of type T from 'Reactive[T] + Behaviour[T]'" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 * beh2).isInstanceOf[Reactive[Int]] mustBe true
		}
		
		"create a new Behaviour with a combined rule" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Behaviour(time => 5)
			
			(beh1 * beh2).now mustBe 25
		}
		
		"create a Behaviour that's rule is dynamic (not evaluated during addition)" in {
			val beh1 = new NumReactive(new Reactive(() => 5.0))
			val beh2 = new Behaviour(time => time)
			val beh = beh1 * beh2
			
			val first_val = beh.now
			Thread.sleep(1)
			val second_val = beh.now
			
			first_val must_!= second_val
		}
		
		"create a new Reactive of type T from 'Reactive[T] + Reactive[T]'" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Reactive(() => 5)
			
			(beh1 * beh2).isInstanceOf[Reactive[Int]] mustBe true
		}
		
		"create a new Reactive with a combined rule" in {
			val beh1 = new NumReactive(new Reactive(() => 5))
			val beh2 = new Reactive(() => 5)
			
			(beh1 * beh2).now mustBe 25
		}
		
		"create a Reactive that's rule is dynamic (not evaluated during addition)" in {
			var x = 0
			val beh1 = new NumReactive(new Reactive(() => x))
			val beh2 = new Reactive(() => 5)
			val beh = beh1 * beh2
			
			val first_val = beh.now
			x = 2	
			val second_val = beh.now
			
			first_val must_!= second_val
		}
	}
}