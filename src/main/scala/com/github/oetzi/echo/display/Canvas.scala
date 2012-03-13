package com.github.oetzi.echo.display

import com.github.oetzi.echo.core.Behavior
import java.awt.Component

trait Canvas {
  protected var widthBeh: Behavior[Int] = new Behavior(t => this.swingComponent().getWidth)
  protected var heightBeh: Behavior[Int] = new Behavior(t => this.swingComponent().getHeight)

  def width(): Behavior[Int] = {
    this.widthBeh
  }

  def height(): Behavior[Int] = {
    this.heightBeh
  }

  protected[display] def swingComponent(): Component

  protected[display] def draw()
}