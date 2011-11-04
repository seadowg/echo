import org.specs._
import com.github.oetzi.echo.core.Reactive
import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.NonDeterminismException

object ReactiveSpec extends Specification {
	"Reactive" should {
		"be created when passed an Event and an initial value" in {
			val step = new Reactive(0, new Event[Int])
			step.isInstanceOf[Reactive[Int]] mustBe true
		}
		
		"return the initial value for when 'now' is called" in {
			val step = new Reactive(0, new Event[Int])
			step.now mustBe 0
		}
		
		"change the current rule to the Event value when it occurs" in {
			val event = new Event[Int]
			val step = new Reactive(0, event)
			
			event.occur(5)
			step.now mustBe 5
		}
		
		"throw an exception when `at` is called" in {
			val beh = new Reactive(5, new Event[Int]())
			beh.at(10) must throwA[NonDeterminismException]
		}
	}
}