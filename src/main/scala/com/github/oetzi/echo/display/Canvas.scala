import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.Occurrence

package com.github.oetzi.echo.display {

trait Canvas {
  val redraw: Event[Unit]

  def width(): Behaviour[Int]

  def height(): Behaviour[Int]

  def update(occurrence: Occurrence[Unit], draw: Boolean)

  def draw(occurrence: Occurrence[Unit])
}

}