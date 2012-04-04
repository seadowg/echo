package com.github.oetzi.echo.display

import com.github.oetzi.echo.core.Behaviour
import java.awt.Component

/**Trait for representing graphical
 * FRP components with a width and height. Usually
 * acts as a wrapper for a Java Swing Component.
 */
trait Canvas {
  protected var widthBeh: Behaviour[Int] = new Behaviour(t => this.swingComponent().getWidth)
  protected var heightBeh: Behaviour[Int] = new Behaviour(t => this.swingComponent().getHeight)

  /**Width of this canvas.
   */
  def width(): Behaviour[Int] = {
    this.widthBeh
  }

  /**Height of this canvas.
   */
  def height(): Behaviour[Int] = {
    this.heightBeh
  }

  /**Returns underlying Swing Component for this Canvas.
   */
  protected[display] def swingComponent(): Component

  /**Draws this component by sampling attributes
   * at the current Echo time.
   */
  protected[display] def draw()
}