package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.core._
import com.github.oetzi.echo.types.Stepper


object StepperSpec extends Specification {
  "Stepper" should {
    /*"have an at" >> {
      "returning 'initial' if event hasn't occured" in {
        val stepper = new Stepper(0, new Event[Int])

        stepper.at(0) mustBe 0
      }

      "returning the newest event if time is >= last event occurrence" in {
        val event = new Event[Int]
        event.occur(new Occurrence(1, 5))
        val stepper = new Stepper(0, event)

        stepper.at(1) mustBe 5
      }

      "returning the initial value if the time is < the first event" in {
        val event = new Event[Int]
        event.occur(new Occurrence(1, 5))
        val stepper = new Stepper(0, event)

        stepper.at(0) mustBe 0
      }

      "returning an event's value if the time is equal to it" in {
        val event = new Event[Int]
        event.occur(new Occurrence(5, 10))
        event.occur(new Occurrence(7, 11))
        val stepper = new Stepper(0, event)

        stepper.at(5) mustBe 10
      }

      "returning an events value if it has the max before time" in {
        val event = new Event[Int]
        event.occur(new Occurrence(5, 10))
        event.occur(new Occurrence(4, 9))
        event.occur(new Occurrence(6, 11))
        val stepper = new Stepper(0, event)

        stepper.at(5) mustBe 10
      }
    } */
  }
}