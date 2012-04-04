package com.github.oetzi.echo.display

import com.github.oetzi.echo.core._
import javax.swing.event.{ChangeEvent, ChangeListener}
import java.awt.Component
import javax.swing.JSlider

/** Class for creating variable slider controls. Wrapper
  * around Swing's JSlider.
 */
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

  /** Returns the value of the Slider (the bar's position).
   */
  val value: Behaviour[Int] = new Stepper(internal.getValue, internal.asInstanceOf[EventSource[Int]])

  protected[display] def draw() {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())

    this.internal.repaint()
  }

  protected[display] def swingComponent(): Component = {
    internal
  }
}

object Slider {
  
  /** Returns a Slider with the bar positioned in the center.
   */
  def apply() = {
    new Slider()
  }
}