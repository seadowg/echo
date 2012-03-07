package com.github.oetzi.echo.display {

import com.github.oetzi.echo.Echo._
import java.util.{TimerTask, Timer}
import javax.swing.{BoxLayout, JFrame}
import java.awt.event.{MouseEvent, MouseMotionListener}
import java.awt.Point
import com.github.oetzi.echo.core.{EventSource, Occurrence, Behavior}
import com.github.oetzi.echo.types.Stepper

class Frame private(private val visibleBeh: Behavior[Boolean]) extends Canvas {
  private var components: List[Canvas] = List[Canvas]()

  val internal: JFrame = new JFrame() {
    setLocationRelativeTo(null)

    override def repaint() {
      Frame.this.update(now())
      super.repaint()
    }
  }

  startClock()

  def startClock() {
    new Timer().schedule(new TimerTask() {
      override def run() {
        val time = now()
        Frame.this.update(time)
        Frame.this.draw(time)
      }
    }, 0, 40)
  }

  private var mouseBeh: Behavior[Point] = null

  def mouse(): Behavior[Point] = {
    if (this.mouseBeh == null) {
      val mouseListener = new MouseMotionListener with EventSource[Point] {
        def mouseDragged(event: MouseEvent) {
          //nothing  
        }

        def mouseMoved(event: MouseEvent) {
          occur(event.getWhen, event.getPoint)
        }
      }
      
      this.mouseBeh = new Stepper(new Point(0, 0), mouseListener)
      this.internal.addMouseMotionListener(mouseListener)
    }

    mouseBeh
  }

  def visible(): Behavior[Boolean] = {
    visibleBeh
  }

  def update(time : Time) {
    this.components.foreach {
      canvas =>
        canvas.update(time)
    }
  }

  private var lastVis = false

  def draw(time : Time) {
    this.internal.setSize(widthBeh.at(time), heightBeh.at(time))

    val vis = visibleBeh.at(time)
    if (vis != lastVis) {
      this.internal.setVisible(vis)
      lastVis = vis
    }

    this.internal.repaint()

    this.components.foreach {
      canvas =>
        canvas.draw(time)
    }
  }
}

object Frame {
  def apply(width: Behavior[Int], height: Behavior[Int], components: List[Canvas] = List(),
            visible: Behavior[Boolean] = true): Frame = {
    val frame = new Frame(visible)

    def insets = frame.internal.getInsets
    frame.widthBeh = width.map(width => width + insets.left + insets.right)
    frame.heightBeh = height.map(height => height + insets.top + insets.bottom)
    frame.internal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.internal.setResizable(false)
    frame.internal.setLayout(new BoxLayout(frame.internal.getContentPane, BoxLayout.Y_AXIS))

    components.foreach {
      component =>
        frame.internal.getContentPane.add(component.internal)
        frame.components = frame.components ++ List(component)
    }

    frame
  }
}

}