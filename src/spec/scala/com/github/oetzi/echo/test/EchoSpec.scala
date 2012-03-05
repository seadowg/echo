package com.github.oetzi.echo.test

import org.specs._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core._

object EchoSpec extends Specification {
  "Echo" should {
    "allow values to lifted to constant Behaviours" in {
      val beh: Behaviour[Int] = 5
      beh must_!= null
    }

    "has a freezeTime function" >> {
      "that exectues the passed block" in {
        var done = false

        freezeTime(0) { () =>
          done = true
        }

        done mustBe true
      }

      "that freezes time during execution of its block" in {
        val value = freezeTime(0) { () =>
          now
        }

        value mustEqual 0
      }
    }
  }
}