package com.github.oetzi.echo.test

import help.TestEvent
import com.github.oetzi.echo.Echo._

import org.specs._

object EventSpec extends Specification {
  "EventSource" should {
    "be empty initially" in {
      val event = new TestEvent[Unit]
      
      event.top(now) mustEqual None
    }
  }
}