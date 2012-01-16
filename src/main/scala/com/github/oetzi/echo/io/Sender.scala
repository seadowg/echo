package com.github.oetzi.echo.io

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.{Occurrence, Event, EventSource}
import java.io.PrintWriter
import actors.Actor

class Sender(val ip: String, val port: Int, event: EventSource[String]) {
  val errors: Event[Exception] = Event[Exception]

  event.map {
    occ =>
      try {
        SenderActor ! occ.value
      }

      catch {
        case e: Exception => this.errors.occur(new Occurrence(now, e))
      }
      occ
  }

  private object SenderActor extends Actor {
    def act = {
      loop {
        react {
          case message: String => sendToSocket(message)
        }
      }
    }

    def sendToSocket(message: String) {
      val socket = new java.net.Socket(ip, port)
      val out = new PrintWriter(socket.getOutputStream(), true)
      out.println(message)
      out.close()
      socket.close()
    }
  }

}