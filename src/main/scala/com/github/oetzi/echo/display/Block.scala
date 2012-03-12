package com.github.oetzi.echo.display

import javax.swing.JPanel
import com.github.oetzi.echo.Echo._
import java.awt.Color
import java.awt.Component
import com.github.oetzi.echo.core.{Behavior, Occurrence}

class Block private() extends Canvas {
  private var components: List[Canvas] = List[Canvas]()

  protected[echo] val internal: JPanel = new JPanel() {
    override def repaint() {
      super.repaint()
    }
  }

  private var colorBeh: Behavior[Color] = this.internal.getBackground

  def color() {
    colorBeh
  }

  protected[display] def draw() {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())
    this.internal.setBackground(colorBeh.eval())

    this.internal.repaint()

    this.components.foreach {
      canvas =>
        canvas.draw()
    }
  }

	protected[display] def swingComponent() : Component = {
		internal
	}
}

object Block {
  def apply(width: Behavior[Int], height: Behavior[Int], color: Behavior[Color] = Color.GRAY,
            components: List[Canvas] = List()): Block = {
    val block = new Block()

    block.widthBeh = width
    block.heightBeh = height
    block.colorBeh = color

    components.foreach {
      component =>
        block.internal.add(component.swingComponent)
        block.components = block.components ++ List(component)
    }

    block
  }
}