package com.github.oetzi.echo.display

import javax.swing.JLabel
import com.github.oetzi.echo.core.{Behavior, Occurrence}
import com.github.oetzi.echo.Echo._

class Text private(private val textBeh : Behavior[String]) extends Canvas {
  val internal : JLabel = new JLabel() {
    override def repaint() {
      Text.this.update(now())
      super.repaint()
    }
  }

  def update(time: Time) {

  }

  def draw(time: Time) {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())
    this.internal.setText(textBeh.eval())

    this.internal.repaint()
  }
}

object Text {
  def apply(text : Behavior[String]) : Text = {
    new Text(text)
  }
}
