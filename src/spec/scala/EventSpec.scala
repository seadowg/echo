import org.specs._
import com.github.oetzi.echo.core.Event
import java.util.Date

object EventSpec extends Specification {
	"Event" should {
		"be created without paramaters" in {
			val event = new Event[Int]
			event.isInstanceOf[Event[Int]] mustBe true
		}
	}
}