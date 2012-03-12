package com.github.oetzi.echo.display

import com.github.oetzi.echo.Echo._
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JTextField
import com.github.oetzi.echo.core.{Behavior, Stepper, EventSource}

class Field private() {
  protected[echo] val internal = new JTextField with EventSource[String] {
    this.addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) {
        occur(getText())
      }
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
  def apply() {
    new Field()
  }
}