import javax.swing._
import java.awt.event._
import com.github.oetzi.echo.core.EventSource

package display {
	class Button(label : String) extends JButton(label) with EventSource[Boolean] {
		super.addActionListener(new ActionListener() {
			def actionPerformed(actionEvent : ActionEvent) {
				Button.this.occur(true)
			}
		});
	}
}