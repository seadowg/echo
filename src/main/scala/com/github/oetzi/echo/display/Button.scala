package com.github.oetzi.echo.display

import javax.swing.JButton
import com.github.oetzi.echo.core.{Occurrence, Behaviour, Event}
import com.github.oetzi.echo.Echo._
import java.awt.event.{ActionEvent, ActionListener}

class Button private() extends Canvas {
  val redraw: Event[Unit] = new Event[Unit]
  val click: Event[Unit] = new Event[Unit]

  val internal: JButton = new JButton() {
    this.addActionListener(new ActionListener() {
      def actionPerformed(event: ActionEvent) {
        Button.this.click.occur(new Occurrence(event.getWhen(), ()))
      }
    })

    override def repaint() {
      Button.this.update(new Occurrence(now, ()))
      super.repaint()
    }
  }

  private var textBeh: Behaviour[String] = new Behaviour(t => this.internal.getText())

  def text(): Behaviour[String] = {
    this.textBeh
  }

  def update(occurrence: Occurrence[Unit], draw: Boolean = false) {
    redraw.occur(occurrence)

    if (draw) this.draw(occurrence)
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