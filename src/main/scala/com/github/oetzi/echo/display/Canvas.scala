package com.github.oetzi.echo.display

import com.github.oetzi.echo.core.Behaviour
import java.awt.Component

trait Canvas {
  protected var widthBeh: Behaviour[Int] = new Behaviour(t => this.swingComponent().getWidth)
  protected var heightBeh: Behaviour[Int] = new Behaviour(t => this.swingComponent().getHeight)

  def width(): Behaviour[Int] = {
    this.widthBeh
  }

  def height(): Behaviour[Int] = {
    this.heightBeh
  }

  protected[display] def swingComponent(): Component

  protected[display] def draw()
}