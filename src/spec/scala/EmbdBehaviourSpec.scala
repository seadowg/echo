import org.specs._
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.EmbdBehaviour

object EmbdBehaviourSpec extends Specification {
	"EmbdBehaviour" should {
		"create a new instance when given a valid Behaviour" in {
			val beh = new Behaviour(time => 5)
			val embd = new EmbdBehaviour(beh)
			
			embd.isInstanceOf[EmbdBehaviour[Int]] mustBe true
		}
		
		"have a rule that matches that of the passed in Behaviour" in {
			val beh = new Behaviour(time => 5)
			val embd = new EmbdBehaviour(beh)
			
			beh.now mustBe embd.now
		}
	}
}