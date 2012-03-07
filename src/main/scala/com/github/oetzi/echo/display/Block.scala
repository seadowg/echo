package com.github.oetzi.echo.display

import javax.swing.JPanel
import com.github.oetzi.echo.Echo._
import java.awt.Color
import com.github.oetzi.echo.core.{Behavior, Occurrence}

class Block private() extends Canvas {
  private var components: List[Canvas] = List[Canvas]()

  val internal: JPanel = new JPanel() {
    override def repaint() {
      Block.this.update(now())
      super.repaint()
    }
  }

  private var colorBeh: Behavior[Color] = this.internal.getBackground

  def color() {
    colorBeh
  }

  def update(time: Time) {
    this.components.foreach {
      canvas =>
        canvas.update(time)
    }
  }

  def draw(time: Time) {
    this.internal.setSize(widthBeh.eval(), heightBeh.eval())
    this.internal.setBackground(colorBeh.eval())

    this.internal.repaint()

    this.components.foreach {
      canvas =>
        canvas.draw(time)
    }
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
        block.internal.add(component.internal)
        block.components = block.components ++ List(component)
    }

    block
  }
}