import java.awt.Color
import javax.swing.JPanel
import com.github.oetzi.echo.Behaviour
import com.github.oetzi.echo.EventSource

package display {
	class ColorPanel extends JPanel {
		def setBackground(color : Behaviour[Color]) {
			new Observer(color).each(event => super.setBackground(event))	
		}
	}
	
	class Observer[T](val behaviour : Behaviour[T]) extends EventSource[T] {
		new Thread(new Runnable() {
			def run() {
				var last = null
				while (true) {
					val now = behaviour.now
					if (last != now) {
						Observer.this.occur(now)
					}
					Thread.sleep(20)
				}
			}
		}).start()
	}
}