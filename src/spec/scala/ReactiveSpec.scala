import org.specs._
import com.github.oetzi.echo.core.Reactive

object ReactiveSpec extends Specification {
	"Reactive" should {
		"create a new instance when passed a correct rule" in {
			val react = new Reactive(() => 5)
			react.isInstanceOf[Reactive[Int]] mustBe true
		}
	}
	
	"Reactive.now" should {
		"return the current value of the rule" in {
			val value = 5
			val react = new Reactive(() => value)
			react.now mustBe value
		}
	}
}