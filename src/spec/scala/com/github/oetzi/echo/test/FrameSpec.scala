package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.display.Frame
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.Behaviour

object FrameSpec extends Specification {
  "Frame" should {
    "have an internal JFrame" in {
      val frame = new Frame()
      frame.internal must_!= null
    }

    "have a redraw event" >> {
      "that exists" in {
        val frame = new Frame()
        frame.redraw must_!= null
      }

      "has one occurrences" >> {
        val frame = new Frame()
        frame.redraw.occs().length mustBe 1
      }
    }

    "have an initial width" in {
      val frame = new Frame()
      frame.width.at(now) mustBe frame.internal.getWidth()
    }

    "have an initial height" in {
      val frame = new Frame()
      frame.height.at(now) mustBe frame.internal.getHeight()
    }

    "have an initial value for 'visible'" in {
      val frame = new Frame()
      frame.visible.at(now) mustBe frame.internal.isVisible()
    }

    "have a setWidth function" >> {
      "that changes the Behaviour value of 'width'" in {
        val frame = new Frame()
        frame.setWidth(new Behaviour(time => 5))

        frame.width().at(now) mustBe 5
      }
    }

    "have a setHeight function" >> {
      "that changes the Behaviour value of 'height'" in {
        val frame = new Frame()
        frame.setHeight(new Behaviour(time => 5))

        frame.height().at(now) mustBe 5
      }
    }

    "have a setVisible function" >> {
      "that changes the Behaviour value of 'visible'" in {
        val frame = new Frame()
        frame.setVisible(new Behaviour(time => true))

        frame.visible().at(now) mustBe true
      }
    }
  }
}