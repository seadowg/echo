import java.awt.Color
import javax.swing.JPanel
import com.github.oetzi.echo.Behaviour

package display {
	class ColorPanel extends JPanel {
		def setBackground(color : Behaviour[Color]) {
			new Thread(new Runnable {
				def run() {
					var last = null
					while (true) {
						val now = color.now
						if (last != now) {
							ColorPanel.super.setBackground(now)
						}
						Thread.sleep(20)
					}
				}
			}).start()	
		}
	}
	
	class Observer[T] extends EventSource[T] {
		//this will replace the code in setBackground
	}
}