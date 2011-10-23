import org.specs._
import com.github.oetzi.echo.core.Stepper
import com.github.oetzi.echo.core.Event

object StepperSpec extends Specification {
	"Stepper" should {
		"be created when passed an Event and an initial value" in {
			val step = new Stepper(0, new Event[Int])
			step.isInstanceOf[Stepper[Int]] mustBe true
		}
		
		"return the initial value for when 'now' is called" in {
			val step = new Stepper(0, new Event[Int])
			step.now mustBe 0
		}
		
		"change the current rule to the Event value when it occurs" in {
			val event = new Event[Int]
			val step = new Stepper(0, event)
			
			event.occur(5)
			step.now mustBe 5
		}
	}
}