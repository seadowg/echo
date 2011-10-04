import javax.swing._

package display {
	object Display {
		def main(args : Array[String]) {
			val button = new Button("Click Me!")
			button.press.each(press => println("OH YEAH YOU CLICKED!"))
			
			val panel = new JPanel()
			val frame = new JFrame()
			panel.add(button)
			frame.add(panel, 0)
			
			frame.setSize(100, 70)
			frame.setLocationRelativeTo(null);
			frame.setVisible(true)
		}
	}
}