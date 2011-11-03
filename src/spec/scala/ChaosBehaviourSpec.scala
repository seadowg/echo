import org.specs._
import com.github.oetzi.echo.core.ChaosBehaviour

object ChaosBehaviourSpec extends Specification {
	"ChoasBehaviour" should {
		"return a new instance when passed a correct rule" in {
			val beh = new ChaosBehaviour(() => 5)
			beh.isInstanceOf[ChaosBehaviour[Int]] mustBe true
		}
		
		"be cool with passed functions" in {
			val beh = new ChaosBehaviour(System.currentTimeMillis)
			beh.isInstanceOf[ChaosBehaviour[Long]] mustBe true
		}
	}
	
	"'now' function" should {
		"return a value of the passed rule" in {
			val beh = new ChaosBehaviour(() => 5)
			beh.now mustBe 5
		}
	}
}