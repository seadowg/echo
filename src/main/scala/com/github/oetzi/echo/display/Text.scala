package com.github.oetzi.echo.display

import javax.swing.JLabel
import com.github.oetzi.echo.core.{Behaviour, Occurrence}
import com.github.oetzi.echo.Echo._

class Text private(private val textBeh : Behaviour[String]) extends Canvas {
  val internal : JLabel = new JLabel() {
    override def repaint() {
      Text.this.update(new Occurrence(now, ()))
      super.repaint()
    }
  }

  def update(occurrence: Occurrence[Unit]) {

  }

  def draw(occurrence: Occurrence[Unit]) {
    this.internal.setSize(widthBeh.at(occurrence.time), heightBeh.at(occurrence.time))
    this.internal.setText(textBeh.at(occurrence.time))

    this.internal.repaint()
  }
}

object Text {
  def apply(text : Behaviour[String]) : Text = {
    new Text(text)
  }
}
