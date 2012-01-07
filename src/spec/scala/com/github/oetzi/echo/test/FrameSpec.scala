package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.display.Frame
import com.github.oetzi.echo.Echo._

object FrameSpec extends Specification {
  "Frame" should {

    val frame = Frame(100, 100)

    "have an internal JFrame" in {
      frame.internal must_!= null
    }

    "have a redraw event" in {
      frame.redraw must_!= null
    }

    "have a width" in {
      frame.width.at(now) must_!= null
    }

    "have a height" in {
      frame.height.at(now) must_!= null
    }

    "be 'visible'" in {
      frame.internal.isVisible() mustBe true
    }
  }
}