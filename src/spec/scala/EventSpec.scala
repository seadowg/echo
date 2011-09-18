import org.specs._
import com.github.oetzi.echo.Event

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
		"execute assigned 'edges'" in {
			val event = new Event[Int]
			var x = 5
			
			event.each(event => x = event)
			event.occur(10)
			x mustBe 10
		}
	}
}