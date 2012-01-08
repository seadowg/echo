package com.github.oetzi.echo.display

import javax.swing.JPanel
import com.github.oetzi.echo.Echo._
import java.awt.Color
import com.github.oetzi.echo.core.{Behaviour, Occurrence}

class Block private() extends Canvas {
  private var components: List[Canvas] = List[Canvas]()

  val internal: JPanel = new JPanel() {
    override def repaint() {
      Block.this.update(new Occurrence(now, ()))
      super.repaint()
    }
  }

  private var colorBeh: Behaviour[Color] = this.internal.getBackground()

  def color() {
    colorBeh
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
    this.internal.setBackground(colorBeh.at(occurrence.time))

    this.internal.repaint()

    this.components.foreach {
      canvas =>
        canvas.draw(occurrence)
    }
  }
}

object Block {
  def apply(width: Behaviour[Int], height: Behaviour[Int], color: Behaviour[Color] = Color.GRAY,
            map: Map[String, Canvas] = Map()): Block = {
    val block = new Block()

    block.widthBeh = width
    block.heightBeh = height
    block.colorBeh = color

    map.foreach {
      entry =>
        block.internal.add(entry._2.internal)
        block.components = block.components ++ List(entry._2)
    }

    block
  }
}