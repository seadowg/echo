package com.github.oetzi.echo.test

import com.github.oetzi.echo.display.Button
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._
import org.specs.Specification

object ButtonSpec extends Specification {
	
	devMode()
	
  "Button" should {

    val button = Button("Click Me")

    "have an internal Button" in {
      button.internal must_!= null
    }

    "have text" in {
      button.text must_!= null
    }
  }
}