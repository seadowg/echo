package com.github.oetzi.echo.display

import com.github.oetzi.echo.types.Stepper
import javax.swing.event.{ChangeEvent, ChangeListener}
import javax.swing.JSlider
import com.github.oetzi.echo.core.{EventSource, Behaviour, Occurrence}
import com.github.oetzi.echo.Echo._


class Slider private() extends Canvas {
  val internal: JSlider = new JSlider() with EventSource[Int] {
    this.addChangeListener(new ChangeListener() {
      def stateChanged(e: ChangeEvent) {
        occur(new Occurrence(now, internal.getValue))
      }
    })

    override def repaint() {
      Slider.this.update(new Occurrence(now, ()))
      super.repaint()
    }
  }

  val value: Behaviour[Int] = new Stepper(internal.getValue, internal.asInstanceOf[EventSource[Int]])

  def update(occurrence: Occurrence[Unit]) {
    redraw.occur(occurrence)
  }

  def draw(occurrence: Occurrence[Unit]) {
    this.internal.setSize(widthBeh.at(occurrence.time), heightBeh.at(occurrence.time))

    this.internal.repaint()
  }
}

object Slider {
  def apply() = {
    new Slider()
  }
}