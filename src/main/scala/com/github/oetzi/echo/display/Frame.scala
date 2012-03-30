package com.github.oetzi.echo.display {

import com.github.oetzi.echo.Echo._
import java.util.{TimerTask, Timer}
import javax.swing.{BoxLayout, JFrame}
import java.awt.event.{MouseEvent, MouseMotionListener}
import java.awt.Point
import java.awt.Component
import com.github.oetzi.echo.core.{Stepper, EventSource, Behaviour}

class Frame private(private val visibleBeh: Behaviour[Boolean]) extends Canvas {
  private var components: List[Canvas] = List[Canvas]()
  private var lastVis = false

  private val internal: JFrame = new JFrame() {
    setLocationRelativeTo(null)

    override def repaint() {
      super.repaint()
    }
  }

  private val mouseListener = new MouseMotionListener with EventSource[Point] {
    def mouseDragged(event: MouseEvent) {}

    def mouseMoved(event: MouseEvent) {
      occur(event.getPoint)
    }
  }

  private val mouseBeh: Behaviour[Point] = new Stepper(new Point(0, 0), mouseListener)

  startClock()

  def mouse(): Behaviour[Point] = {
    if (this.internal.getMouseMotionListeners.length < 1) {
      this.internal.addMouseMotionListener(mouseListener)
    }

    mouseBeh
  }

  def visible(): Behaviour[Boolean] = {
    visibleBeh
  }

  protected[display] def draw() {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())

    val vis = visibleBeh.eval()
    if (vis != lastVis) {
      this.internal.setVisible(vis)
      lastVis = vis
    }

    this.internal.repaint()

    this.components.foreach {
      canvas =>
        canvas.draw()
    }
  }

  protected[display] def swingComponent(): Component = {
    internal
  }

  def startClock() {
    new Timer().schedule(new TimerTask() {
      override def run() {
        val time = now()
        Frame.this.draw()
      }
    }, 0, 20)
  }
}

object Frame {
  def apply(width: Behaviour[Int], height: Behaviour[Int], components: List[Canvas] = List(),
            visible: Behaviour[Boolean] = true): Frame = {
    val frame = new Frame(visible)

    def insets = frame.internal.getInsets
    frame.widthBeh = width.map(width => width + insets.left + insets.right)
    frame.heightBeh = height.map(height => height + insets.top + insets.bottom)
    frame.internal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.internal.setResizable(false)
    frame.internal.setLayout(new BoxLayout(frame.internal.getContentPane, BoxLayout.Y_AXIS))

    components.foreach {
      component =>
        frame.internal.getContentPane.add(component.swingComponent())
        frame.components = frame.components ++ List(component)
    }

    frame
  }
}

}