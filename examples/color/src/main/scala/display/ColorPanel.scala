import java.awt.Color
import javax.swing.JPanel
import com.github.oetzi.echo.Behaviour
import com.github.oetzi.echo.Event

package display {
	class ColorPanel extends JPanel {
		def setBackground(color : Behaviour[Color]) {
			val sampler = new Event[Unit]
			
			new Thread(new Runnable() {
				def run {
					while (true) {
						sampler.occur()
						Thread.sleep(40)
					}
				}
			}).start
			
			color.sample(sampler).foreach(change => super.setBackground(change))	
		}
	}
}