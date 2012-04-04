package com.github.oetzi.echo.display

import javax.swing.JPanel
import com.github.oetzi.echo.Echo._
import java.awt.Color
import java.awt.Component
import com.github.oetzi.echo.core.Behaviour

/** Analogue for Swing JPanel. Has a width and height
  * and can hold other Canvas objects.
 */
class Block private() extends Canvas {
  private var components: List[Canvas] = List[Canvas]()

  protected[echo] val internal: JPanel = new JPanel() {
    override def repaint() {
      super.repaint()
    }
  }
  
  private var colorBeh: Behaviour[Color] = this.internal.getBackground

  /** Returns the color of this Block.
   */
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

  protected[display] def swingComponent(): Component = {
    internal
  }
}

object Block {
  
  /** Creates a block with the specified width, height, color
    * and component Canvases. Defaults to grey background if
    * no color is specified.
   */
  def apply(width: Behaviour[Int], height: Behaviour[Int], color: Behaviour[Color] = Color.GRAY,
            components: List[Canvas] = List()): Block = {
    val block = new Block()

    block.widthBeh = width
    block.heightBeh = height
    block.colorBeh = color

    components.foreach {
      component =>
        block.internal.add(component.swingComponent())
        block.components = block.components ++ List(component)
    }

    block
  }
}