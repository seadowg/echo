import com.github.oetzi.echo.core.Event
import com.github.oetzi.echo.core.Behaviour
import com.github.oetzi.echo.core.Occurrence

package com.github.oetzi.echo.display {

trait Canvas {
  val redraw: Event[Unit]

  def width(): Behaviour[Int]

  def height(): Behaviour[Int]

  def visible(): Behaviour[Boolean]

  def setHeight(height: Behaviour[Int])

  def setWidth(width: Behaviour[Int])

  def setVisible(visible: Behaviour[Boolean])

  def update(occurrence: Occurrence[Unit])

  protected def draw(occurrence: Occurrence[Unit])
}

}