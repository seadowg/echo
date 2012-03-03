package com.github.oetzi.echo.display

import javax.swing.JLabel
import com.github.oetzi.echo.core.{Behaviour, Occurrence}
import com.github.oetzi.echo.Echo._

class Text private(private val textBeh : Behaviour[String]) extends Canvas {
  val internal : JLabel = new JLabel() {
    override def repaint() {
      Text.this.update(now())
      super.repaint()
    }
  }

  def update(time: Time) {

  }

  def draw(time: Time) {
    this.internal.setSize(widthBeh.at(time), heightBeh.at(time))
    this.internal.setText(textBeh.at(time))

    this.internal.repaint()
  }
}

object Text {
  def apply(text : Behaviour[String]) : Text = {
    new Text(text)
  }
}
