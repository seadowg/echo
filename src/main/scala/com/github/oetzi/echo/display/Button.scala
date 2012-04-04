package com.github.oetzi.echo.display

import javax.swing.JButton
import java.awt.Component
import com.github.oetzi.echo.core.{Behaviour, Event, EventSource}
import java.awt.event.{ActionEvent, ActionListener}

/** Clickable button type. Utilises Swing JButton.
 */
class Button private() extends Canvas {
  protected[echo] val internal: JButton = new JButton() with EventSource[Unit] {
    this.addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) {
        occur(())
      }
    })

    override def repaint() {
      super.repaint()
    }
  }

  /**Returns an Event that occurs whenever this Button is
   * is clicked.
   */
  val click: Event[Unit] = internal.asInstanceOf[EventSource[Unit]].event()

  private var textBeh: Behaviour[String] = new Behaviour(t => this.internal.getText)

  /**Returns the text displayed on this
   * Button.
   */
  def text(): Behaviour[String] = {
    this.textBeh
  }

  def draw() {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())
    this.internal.setText(textBeh.eval())

    this.internal.repaint()
  }

  protected[display] def swingComponent(): Component = {
    internal
  }
}

object Button {

  /**Creates a Button that is displayed with
   * the specified text.
   */
  def apply(text: Behaviour[String]): Button = {
    val button = new Button()
    button.textBeh = text

    button
  }

  /**Creates a Button that is displayed with the
   * text produced from the given function executed
   * with respect to itself. Allows creation of Button
   * that's text is altered by its own clicks.
   */
  def apply(func: Button => Behaviour[String]) = {
    val button = new Button()
    button.textBeh = func(button)

    button
  }
}