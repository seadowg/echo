package com.github.oetzi.echo.io

import com.github.oetzi.echo.core.EventSource
import java.io.PrintWriter
import actors.Actor

class Sender(val ip: String, val port: Int, event: EventSource[String]) extends Breakable {
  SenderActor.start()

  private object SenderActor extends Actor {
    def act {
      loop {
        react {
          case message: String => sendToSocket(message)
        }
      }
    }

    def sendToSocket(message: String) {
      dangerous {
        () =>
          val socket = new java.net.Socket(ip, port)
          val out = new PrintWriter(socket.getOutputStream, true)
          out.println(message)
          out.close()
          socket.close()
      }
    }
  }

}