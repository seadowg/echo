package com.github.oetzi.echo.test

import org.specs._

object SocketsSpec extends Specification {
  "Sender/Receiver" should {
    /*"send events in the event stream from Sender" in {
      val event = new Event[String]()
      event.occur(new Occurrence(0, "hello"))
      event.occur(new Occurrence(1, "you"))

      val receiver = new Receiver(1992)
      val sender = new Sender("localhost", 1992, event)

      receiver.occs().length mustBe 2
      receiver.die()
    } pendingUntilFixed

    "send events added to the event stream later from Sender" in {
      val event = Event[String]()
      val receiver = new Receiver(1993)
      val sender = new Sender("localhost", 1993, event)

      event.occur(new Occurrence(0, "hello"))

      receiver.errors.occs.length mustBe 0
      receiver.die()
    }

    "creates an error occurrence for exceptions in Sender" in {
      val sender = new Sender("localhost", 1994, Event(0, "hello"))

      sender.errors.occs.length mustBe 1
    }

    "creates an error occurrence for exceptions in Sender" in {
      new Receiver(1992)
      val rec = new Receiver(1992)

      rec.errors.occs.length mustBe 1
    }*/
  }
}