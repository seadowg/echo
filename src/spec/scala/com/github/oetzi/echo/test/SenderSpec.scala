package com.github.oetzi.echo.test

import org.specs.Specification
import com.github.oetzi.echo.io.Sender
import com.github.oetzi.echo.core.Event

object SenderSpec extends Specification {
  "Sender" should {
    "have an errors event" in {
      val socket = new Sender("localhost", 1992, Event[String]())

      socket.errors must_!= null
    }
  }
}