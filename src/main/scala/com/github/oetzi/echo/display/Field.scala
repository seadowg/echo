package com.github.oetzi.echo.display

import com.github.oetzi.echo.Echo._
import javax.swing.JTextField
import com.github.oetzi.echo.core.{Behavior, Stepper, EventSource}
import java.awt.event.{KeyEvent, KeyListener, ActionEvent, ActionListener}

class Field private() extends Canvas {
  protected[echo] val internal = new JTextField with EventSource[String] {
    this.addKeyListener(new KeyListener() {
      def keyReleased(e : KeyEvent) {
        occur(getText())
      }

      def keyPressed(e : KeyEvent) { }

      def keyTyped(e : KeyEvent) { }
    })

    override def repaint() {
      Field.this.update(now())
      super.repaint()
    }
  }
  
  private val textBeh = new Stepper("", internal.asInstanceOf[EventSource[String]].event())

  def text() : Behavior[String] = {
    textBeh
  }

  def update(time: Time) {

  }

  def draw(time: Time) {
    this.internal.repaint()
  }
}

object Field {
  def apply() : Field = {
    new Field()
  }
}