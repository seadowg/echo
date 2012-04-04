package com.github.oetzi.echo.display

import javax.swing.JTextField
import java.awt.Component
import com.github.oetzi.echo.core.{Behaviour, Stepper, EventSource}
import java.awt.event.{KeyEvent, KeyListener}

/** Represents a standard text field. Internall uses a Swing
  * JTextField.
 */
class Field private() extends Canvas {
  private[echo] val internal = new JTextField with EventSource[String] {
    this.addKeyListener(new KeyListener() {
      def keyReleased(e: KeyEvent) {
        occur(getText)
      }

      def keyPressed(e: KeyEvent) {}

      def keyTyped(e: KeyEvent) {}
    })

    override def repaint() {
      super.repaint()
    }
  }

  private val textBeh = new Stepper("", internal.asInstanceOf[EventSource[String]].event())

  /** Returns the text in the field.
   */
  def text(): Behaviour[String] = {
    textBeh
  }

  def draw() {
    this.internal.repaint()
  }

  protected[display] def swingComponent(): Component = {
    internal
  }
}

object Field {
  
  /** Returns a new empty Field.
   */
  def apply(): Field = {
    new Field()
  }
}