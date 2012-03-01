package com.github.oetzi.echo.display

import javax.swing.JButton
import com.github.oetzi.echo.core.{Occurrence, Behaviour, Event, EventSource}
import com.github.oetzi.echo.Echo._
import java.awt.event.{ActionEvent, ActionListener}

class Button private() extends Canvas {
  val internal: JButton = new JButton() with EventSource[Unit] {
    this.addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) {
        occur(event.getWhen(), ())
      }
    })

    override def repaint() {
      Button.this.update(new Occurrence(now, ()))
      super.repaint()
    }
  }
  
  val click: Event[Unit] = internal.asInstanceOf[EventSource[Unit]].event

  private var textBeh: Behaviour[String] = new Behaviour(t => this.internal.getText)

  def text(): Behaviour[String] = {
    this.textBeh
  }

  def update(occurrence: Occurrence[Unit]) {
    
  }

  def draw(occurrence: Occurrence[Unit]) {
    this.internal.setSize(widthBeh.at(occurrence.time), heightBeh.at(occurrence.time))
    this.internal.setText(textBeh.at(occurrence.time))

    this.internal.repaint()
  }
}

object Button {
  def apply(text: Behaviour[String]): Button = {
    val button = new Button()
    button.textBeh = text

    button
  }

  def apply(func: Button => Behaviour[String]) = {
    val button = new Button()
    button.textBeh = func(button)

    button
  }
}