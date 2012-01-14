package com.github.oetzi.echo.display {

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.{Occurrence, Behaviour}
import java.util.{TimerTask, Timer}
import javax.swing.{BoxLayout, JFrame}

class Frame private() extends Canvas {
  private var components: List[Canvas] = List[Canvas]()

  val internal: JFrame = new JFrame() {
    override def repaint() {
      Frame.this.update(new Occurrence(now, ()))
      super.repaint()
    }
  }

  startClock()

  def startClock() {
    new Timer().schedule(new TimerTask() {
      override def run() {
        val occ = new Occurrence(now, ())
        Frame.this.update(occ)
        Frame.this.draw(occ)
      }
    }, 0, 40)
  }

  def update(occurrence: Occurrence[Unit]) {
    redraw.occur(occurrence)

    this.components.foreach {
      canvas =>
        canvas.update(occurrence)
    }
  }

  def draw(occurrence: Occurrence[Unit]) {
    this.internal.setSize(widthBeh.at(occurrence.time), heightBeh.at(occurrence.time))

    this.internal.repaint()

    this.components.foreach {
      canvas =>
        canvas.draw(occurrence)
    }
  }
}

object Frame {
  def apply(width: Behaviour[Int], height: Behaviour[Int], components: List[Canvas] = List()): Frame = {
    val frame = new Frame()

    def insets = frame.internal.getInsets()
    frame.widthBeh = width.map(width => width + insets.left + insets.right)
    frame.heightBeh = height.map(height => height + insets.top + insets.bottom)
    frame.internal.setVisible(true)
    frame.internal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.internal.setResizable(false)
    frame.internal.setLayout(new BoxLayout(frame.internal.getContentPane, BoxLayout.Y_AXIS))

    components.foreach {
      component =>
        frame.internal.getContentPane().add(component.internal)
        frame.components = frame.components ++ List(component)
    }

    frame
  }
}

}