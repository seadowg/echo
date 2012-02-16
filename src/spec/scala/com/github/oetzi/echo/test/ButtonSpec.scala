package com.github.oetzi.echo.test

import com.github.oetzi.echo.display.Button
import com.github.oetzi.echo.Echo._
import org.specs.Specification

object ButtonSpec extends Specification {
  "Button" should {

    val button = Button("Click Me")

    "have an internal Button" in {
      button.internal must_!= null
    }

    "have a redraw event" in {
      button.redraw must_!= null
    }

    "have text" in {
      button.text must_!= null
    }

    "have a click event" >> {
      "that exists" in {
        button.click must_!= null
      }

      "that is empty initially" in {
        button.click.occs().length mustBe 0
      }

      "that occurs every time the JButton is clicked" in {
        val before = button.click.occs().length
        button.internal.doClick()

        button.click.occs().length mustBe before + 1
      }
    }
  }
}