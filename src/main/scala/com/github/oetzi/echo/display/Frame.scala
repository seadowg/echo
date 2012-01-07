package com.github.oetzi.echo.display {

import javax.swing.JFrame
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.{Occurrence, Behaviour, Event}
import java.lang.Thread

class Frame private() extends Canvas {
  val redraw: Event[Unit] = new Event[Unit]()

  val internal: JFrame = new JFrame() {
    override def repaint() {
      Frame.this.update(new Occurrence(now, ()), false)
      super.repaint()
    }
  }
  private val components: List[Canvas] = List[Canvas]()

  private var widthBeh: Behaviour[Int] = this.internal.getWidth()
  private var heightBeh: Behaviour[Int] = this.internal.getHeight()

  startClock()

  def startClock() {
    val thread = new Thread(new Runnable() {
      def run() {
        while (true) {
          Frame.this.update(new Occurrence(now, ()), true)
          Thread.sleep(40)
        }
      }
    })
    thread.start()
  }

  def width(): Behaviour[Int] = {
    this.widthBeh
  }

  def height(): Behaviour[Int] = {
    this.heightBeh
  }

  def update(occurrence: Occurrence[Unit], draw: Boolean) {
    redraw.occur(occurrence)
    if (draw) this.draw(occurrence)

    this.components.foreach {
      canvas =>
        canvas.update(occurrence, draw)
    }
  }

  def draw(occurrence: Occurrence[Unit]) {
    this.internal.setSize(widthBeh.at(occurrence.time), heightBeh.at(occurrence.time))

    this.internal.repaint()
  }
}

object Frame {
  def apply(width: Behaviour[Int], height: Behaviour[Int]): Frame = {
    val frame = new Frame()
    frame.widthBeh = width
    frame.heightBeh = height
    frame.internal.setVisible(true)

    frame
  }
}

}