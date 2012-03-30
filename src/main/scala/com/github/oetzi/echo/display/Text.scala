package com.github.oetzi.echo.display

import javax.swing.JLabel
import java.awt.Component
import com.github.oetzi.echo.core.Behaviour

class Text private(private val textBeh: Behaviour[String]) extends Canvas {
  protected[echo] val internal: JLabel = new JLabel() {
    override def repaint() {
      super.repaint()
    }
  }

  protected[display] def draw() {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())
    this.internal.setText(textBeh.eval())

    this.internal.repaint()
  }

  protected[display] def swingComponent(): Component = {
    internal
  }
}

object Text {
  def apply(text: Behaviour[String]): Text = {
    new Text(text)
  }
}
