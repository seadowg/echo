package com.github.oetzi.echo.display

import javax.swing.JButton
import java.awt.Component
import com.github.oetzi.echo.core.{Behavior, Event, EventSource}
import com.github.oetzi.echo.Echo._
import java.awt.event.{ActionEvent, ActionListener}

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

  val click: Event[Unit] = internal.asInstanceOf[EventSource[Unit]].event()

  private var textBeh: Behavior[String] = new Behavior(t => this.internal.getText)

  def text(): Behavior[String] = {
    this.textBeh
  }

  def draw() {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())
    this.internal.setText(textBeh.eval())

    this.internal.repaint()
  }

	protected[display] def swingComponent() : Component = {
		internal
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