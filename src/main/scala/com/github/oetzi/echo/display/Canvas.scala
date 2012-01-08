import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.Occurrence

package com.github.oetzi.echo.display {

import java.awt.Component

trait Canvas {
  val redraw: Event[Unit]
  val internal: Component

  protected var widthBeh: Behaviour[Int] = new Behaviour(t => this.internal.getWidth())
  protected var heightBeh: Behaviour[Int] = new Behaviour(t => this.internal.getHeight())

  def width(): Behaviour[Int] = {
    this.widthBeh
  }

  def height(): Behaviour[Int] = {
    this.heightBeh
  }

  def update(occurrence: Occurrence[Unit])

  def draw(occurrence: Occurrence[Unit])
}

}