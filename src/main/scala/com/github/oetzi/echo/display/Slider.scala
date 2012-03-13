package com.github.oetzi.echo.display

import com.github.oetzi.echo.core._
import javax.swing.event.{ChangeEvent, ChangeListener}
import java.awt.Component
import javax.swing.JSlider


class Slider private() extends Canvas {
  protected[echo] val internal: JSlider = new JSlider() with EventSource[Int] {
    this.addChangeListener(new ChangeListener() {
      def stateChanged(e: ChangeEvent) {
        occur(internal.getValue)
      }
    })

    override def repaint() {
      super.repaint()
    }
  }

  val value: Behavior[Int] = new Stepper(internal.getValue, internal.asInstanceOf[EventSource[Int]])

  protected[display] def draw() {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())

    this.internal.repaint()
  }

  protected[display] def swingComponent(): Component = {
    internal
  }
}

object Slider {
  def apply() = {
    new Slider()
  }
}