import java.awt.Color
import javax.swing.JFrame
import javax.swing.JPanel
import com.github.oetzi.echo.core.Behaviour

package display {
	object Display {
		def main(args : Array[String]) {
			val button = new Button("BLUE!")
			val color = new Behaviour[Color](time => Color.red).until(button, time => Color.blue)
			
			val panel = new ColorPanel()
			panel.add(button)
			panel.setBackground(color)
			
			makeFrame(panel)
		}
		
		def makeFrame(panel : JPanel) {
			val frame = new JFrame()
			frame.add(panel, 0)
			
			frame.setSize(200, 200)
			frame.setLocationRelativeTo(null);
			frame.setVisible(true)
		}
	}
}