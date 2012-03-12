package com.github.oetzi.echo.display

import com.github.oetzi.echo.Echo._
import javax.swing.JTextField
import java.awt.Component
import com.github.oetzi.echo.core.{Behavior, Stepper, EventSource}
import java.awt.event.{KeyEvent, KeyListener, ActionEvent, ActionListener}

class Field private() extends Canvas {
  private[echo] val internal = new JTextField with EventSource[String] {
    this.addKeyListener(new KeyListener() {
      def keyReleased(e : KeyEvent) {
        occur(getText())
      }

      def keyPressed(e : KeyEvent) { }

      def keyTyped(e : KeyEvent) { }
    })

    override def repaint() {
      super.repaint()
    }
  }
  
  private val textBeh = new Stepper("", internal.asInstanceOf[EventSource[String]].event())

  def text() : Behavior[String] = {
    textBeh
  }

  def draw() {
    this.internal.repaint()
  }

	protected[display] def swingComponent() : Component = {
		internal
	}
}

object Field {
  def apply() : Field = {
    new Field()
  }
}