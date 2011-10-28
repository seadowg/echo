import org.specs._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.Behaviour

object EchoSpec extends Specification {
	"Echo" should {
		"allow values to be composed with Behaviours (via conversion)" in {
			val beh = new Behaviour(time => 5)
			val comp_beh = beh + 5
			comp_beh must_!= null
		}
		
		"convert values to Behaviours with static rule equal to their value" in {
			val beh = new Behaviour(time => 5)
			val comp_beh = beh + 5
			comp_beh.now mustBe 10
		}
		
		"allow numerically typed Behaviors to be composed" in {
			val beh = new Behaviour(time => time)
			val beh2 = new Behaviour(time => time)
			
			val comp_beh = beh + beh2
			comp_beh must_!= null
		}
	}
}