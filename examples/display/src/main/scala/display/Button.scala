import javax.swing._
import java.awt.event._
import com.github.oetzi.echo.Event

package display {
	class Button(label : String) extends JButton(label) {
		def press() : Event[Boolean] = {
			val event = new Event[Boolean]
			super.addActionListener(new ActionListener() {
				def actionPerformed(actionEvent : ActionEvent) {
					event.occur(true)
				}
			});
			
			return event
		}
	}
}