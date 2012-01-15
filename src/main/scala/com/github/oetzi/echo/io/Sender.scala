package com.github.oetzi.echo.io

import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.{Occurrence, Event, EventSource}
import java.io.PrintWriter

class Sender(val ip: String, val port: Int, event: EventSource[String]) {
  val errors: Event[Exception] = Event[Exception]

  event.map {
    occ =>
      try {
        val socket = new java.net.Socket(ip, port)
        val out = new PrintWriter(socket.getOutputStream(), true)
        out.println(occ.value)
        out.close()
        socket.close()
      }

      catch {
        case e: Exception => this.errors.occur(new Occurrence(now, e))
      }
      occ
  }
}