import org.specs._
import com.github.oetzi.echo.Witness
import com.github.oetzi.echo.Behaviour

object WitnessSpec extends Specification {
	"Witness" should {
		"be created when passed a Behaviour" in {
			val witness = new Witness(new Behaviour(time => true))
			witness.isInstanceOf[Witness[Boolean]] mustBe true
		}
		
		"occur when its Behaviour changes value" in {
			val behaviour = new Behaviour(time => false)
			val witness = new Witness(behaviour)
			var fired = false
			
			witness.each(event => fired = event)
			behaviour.change(time => true)
			Thread.sleep(1)
			
			fired mustBe true
		}
		
		"occurs only when the Behaviour changes" in {
			val behaviour = new Behaviour(time => false)
			val witness = new Witness(behaviour)
			var fired = 0
			
			witness.each(event => fired = fired + 1)
			behaviour.change(time => true)
			Thread.sleep(1)
			
			fired mustBe 1
		}
		
		"not crash if each is called more than once" in {
			val behaviour = new Behaviour(time => false)
			val witness = new Witness(behaviour)
			var failed = false
			
			try {
				witness.each(event => 5)
				witness.each(event => 5)
			}
			
			catch {
				case e : Exception => failed = true
			}
			
			failed mustBe false
		}
	}
	
	"Witness.dispose" should {
		"stop the Witness firing when the event changes" in {
			val behaviour = new Behaviour(time => false)
			val witness = new Witness(behaviour)
			var fired = false
			
			witness.each(event => fired = event)
			witness.dispose
			behaviour.change(time => true)
			
			fired mustBe false
		}
	}
}