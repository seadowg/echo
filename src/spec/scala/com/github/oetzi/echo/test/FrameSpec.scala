package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.display.{Button, Canvas, Frame}

object FrameSpec extends Specification {
  "Frame" should {

    val frame = Frame(100, 100, List[Canvas]())

    "have an internal JFrame" in {
      frame.internal must_!= null
    }

    "create a mouse behaviour when prompted" in {
      frame.mouse must_!= null
    }

    "have a width" in {
      frame.width must_!= null
    }

    "have a height" in {
      frame.height must_!= null
    }

    "have a visible'" in {
      frame.visible must_!= null
    }

    "adds components to the JFrame's content" in {
      val button = Button("Hello")
      val frame = Frame(100, 100, List(button))

      frame.internal.getContentPane.getComponent(0) mustBe button.internal
    }
  }
}