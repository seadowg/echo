package com.github.oetzi.echo.display {

import javax.swing.JFrame
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.types.Switcher
import com.github.oetzi.echo.core.{Occurrence, Behaviour, Event}
import java.lang.Thread

class Frame extends Canvas {
  val internal: JFrame = new JFrame()
  val redraw: Event[Unit] = new Event[Unit]()

  private val widthBeh: Switcher[Int] = new Switcher(this.internal.getWidth(), new Event[Behaviour[Int]])
  private val heightBeh: Switcher[Int] = new Switcher(this.internal.getHeight(), new Event[Behaviour[Int]])
  private val visibleBeh: Switcher[Boolean] = new Switcher(this.internal.isVisible(), new Event[Behaviour[Boolean]])

  startClock()

  def startClock() {
    val thread = new Thread(new Runnable() {
      def run() {
        while (true) {
          Frame.this.update(new Occurrence(now, ()))
          Thread.sleep(40)
        }
      }
    })
    thread.start()
  }

  def visible(): Behaviour[Boolean] = {
    this.visibleBeh
  }

  def width(): Behaviour[Int] = {
    this.widthBeh
  }

  def height(): Behaviour[Int] = {
    this.heightBeh
  }

  def setWidth(behaviour: Behaviour[Int]) {
    this.widthBeh.event.occur(new Occurrence(now, behaviour))
  }

  def setHeight(behaviour: Behaviour[Int]) {
    this.heightBeh.event.occur(new Occurrence(now, behaviour))
  }

  def setVisible(behaviour: Behaviour[Boolean]) {
    this.visibleBeh.event.occur(new Occurrence(now, behaviour))
  }

  def update(occurrence: Occurrence[Unit]) {
    redraw.occur(occurrence)
    this.draw(occurrence)
  }

  protected def draw(occurrence: Occurrence[Unit]) {
    this.internal.setVisible(visibleBeh.at(occurrence.time))
    this.internal.setSize(widthBeh.at(occurrence.time), heightBeh.at(occurrence.time))

    this.internal.repaint()
  }
}

}