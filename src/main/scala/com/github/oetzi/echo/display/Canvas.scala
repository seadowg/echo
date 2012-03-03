import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.display {

import java.awt.Component

trait Canvas {
  val internal: Component

  protected var widthBeh: Behaviour[Int] = new Behaviour(t => this.internal.getWidth)
  protected var heightBeh: Behaviour[Int] = new Behaviour(t => this.internal.getHeight)

  def width(): Behaviour[Int] = {
    this.widthBeh
  }

  def height(): Behaviour[Int] = {
    this.heightBeh
  }

  def update(time : Time)

  def draw(time : Time)
}

}