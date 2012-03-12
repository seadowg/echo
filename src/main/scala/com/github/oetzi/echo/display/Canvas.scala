import com.github.oetzi.echo.core.Behavior
import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.display {

import java.awt.Component

trait Canvas {
  protected[echo] val internal: Component

  protected var widthBeh: Behavior[Int] = new Behavior(t => this.internal.getWidth)
  protected var heightBeh: Behavior[Int] = new Behavior(t => this.internal.getHeight)

  def width(): Behavior[Int] = {
    this.widthBeh
  }

  def height(): Behavior[Int] = {
    this.heightBeh
  }

  def update(time : Time)

  def draw(time : Time)
}

}