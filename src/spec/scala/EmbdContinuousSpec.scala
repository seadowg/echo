import org.specs._
import com.github.oetzi.echo.core.Continuous
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.EmbdContinuous

object EmbdContinuousSpec extends Specification {
	"EmbdContinuous" should {
		"create a new instance when given a valid Continuous" in {
			val beh = new Behaviour(time => 5)
			val embd = new EmbdContinuous(beh)
			
			embd.isInstanceOf[EmbdContinuous[Int]] mustBe true
		}
	}
}