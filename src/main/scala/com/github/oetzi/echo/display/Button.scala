package com.github.oetzi.echo.display

import javax.swing.JButton
import com.github.oetzi.echo.core.{Behavior, Event, EventSource}
import com.github.oetzi.echo.Echo._
import java.awt.event.{ActionEvent, ActionListener}

class Button private() extends Canvas {
  val internal: JButton = new JButton() with EventSource[Unit] {
    this.addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) {
        occur(event.getWhen, ())
      }
    })

    override def repaint() {
      Button.this.update(now())
      super.repaint()
    }
  }

  val click: Event[Unit] = internal.asInstanceOf[EventSource[Unit]].event()

  private var textBeh: Behavior[String] = new Behavior(t => this.internal.getText)

  def text(): Behavior[String] = {
    this.textBeh
  }

  def update(time: Time) {

  }

  def draw(time: Time) {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())
    this.internal.setText(textBeh.eval())

    this.internal.repaint()
  }
}

object Button {
  def apply(text: Behavior[String]): Button = {
    val button = new Button()
    button.textBeh = text

    button
  }

  def apply(func: Button => Behavior[String]) = {
    val button = new Button()
    button.textBeh = func(button)

    button
  }
}