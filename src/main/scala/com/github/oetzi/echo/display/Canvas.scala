import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.Occurrence

package com.github.oetzi.echo.display {

import java.awt.Component

trait Canvas {
  val redraw: Event[Unit]
  val internal: Component

  def width(): Behaviour[Int]

  def height(): Behaviour[Int]

  def update(occurrence: Occurrence[Unit], draw: Boolean = false)

  def draw(occurrence: Occurrence[Unit])
}

}