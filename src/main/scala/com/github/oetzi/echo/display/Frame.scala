package com.github.oetzi.echo.display {

import javax.swing.JFrame
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.{Occurrence, Behaviour, Event}
import java.util.{TimerTask, Timer}

class Frame private() extends Canvas {
  val redraw: Event[Unit] = new Event[Unit]()

  val internal: JFrame = new JFrame() {
    override def repaint() {
      Frame.this.update(new Occurrence(now, ()))
      super.repaint()
    }
  }
  private var components: List[Canvas] = List[Canvas]()

  startClock()

  def startClock() {
    new Timer().schedule(new TimerTask() {
      override def run() {
        Frame.this.update(new Occurrence(now, ()), true)
      }
    }, 0, 40)
  }

  def update(occurrence: Occurrence[Unit], draw: Boolean = false) {
    redraw.occur(occurrence)

    this.components.foreach {
      canvas =>
        canvas.update(occurrence)
    }

    if (draw) {
      this.draw(occurrence)
      this.components.foreach {
        canvas =>
          canvas.draw(occurrence)
      }
    }
  }

  def draw(occurrence: Occurrence[Unit]) {
    this.internal.setSize(widthBeh.at(occurrence.time), heightBeh.at(occurrence.time))

    this.internal.repaint()
  }
}

object Frame {
  def apply(width: Behaviour[Int], height: Behaviour[Int], map: Map[String, Canvas]): Frame = {
    val frame = new Frame()
    frame.widthBeh = width
    frame.heightBeh = height
    frame.internal.setVisible(true)
    frame.internal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.internal.setResizable(false)

    map.foreach {
      entry =>
        frame.internal.getContentPane().add(entry._2.internal)
        frame.components = frame.components ++ List(entry._2)
    }

    frame
  }
}

}