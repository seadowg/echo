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
	}
}