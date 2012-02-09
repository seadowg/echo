package com.github.oetzi.echo.display {

import com.github.oetzi.echo.Echo._
import java.util.{TimerTask, Timer}
import javax.swing.{BoxLayout, JFrame}
import java.awt.event.{MouseEvent, MouseMotionListener}
import java.awt.Point
import com.github.oetzi.echo.core.{Event, Occurrence, Behaviour}
import com.github.oetzi.echo.types.Stepper

class Frame private(private val visibleBeh: Behaviour[Boolean]) extends Canvas {
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

  private var mouseBeh: Behaviour[Point] = null

  def mouse(): Behaviour[Point] = {
    if (this.mouseBeh == null) {
      val mouseEvent = new Event[Point]
      this.mouseBeh = new Stepper(new Point(0, 0), mouseEvent)

      val mouseListener = new MouseMotionListener {
        def mouseDragged(event: MouseEvent) {
          //nothing  
        }

        def mouseMoved(event: MouseEvent) {
          mouseEvent.occur(new Occurrence(event.getWhen(), event.getPoint()))
        }
      }
    }

    mouseBeh
  }

  def visible(): Behaviour[Boolean] = {
    visibleBeh
  }

  def update(occurrence: Occurrence[Unit]) {
    redraw.occur(occurrence)

    this.components.foreach {
      canvas =>
        canvas.update(occurrence)
    }
  }
  
  private var lastVis = false

  def draw(occurrence: Occurrence[Unit]) {
    this.internal.setSize(widthBeh.at(occurrence.time), heightBeh.at(occurrence.time))
    
    val vis = visibleBeh.at(occurrence.time)
    if (vis != lastVis) {
      this.internal.setVisible(vis)
      lastVis = vis
    }

    this.internal.repaint()

    this.components.foreach {
      canvas =>
        canvas.draw(occurrence)
    }
  }
}

object Frame {
  def apply(width: Behaviour[Int], height: Behaviour[Int], components: List[Canvas] = List(),
            visible: Behaviour[Boolean] = true): Frame = {
    val frame = new Frame(visible)

    def insets = frame.internal.getInsets()
    frame.widthBeh = width.map(width => width + insets.left + insets.right)
    frame.heightBeh = height.map(height => height + insets.top + insets.bottom)
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