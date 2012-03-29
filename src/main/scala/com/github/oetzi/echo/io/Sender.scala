package com.github.oetzi.echo.io

import actors.Actor
import com.github.oetzi.echo.core._
import java.io.{InputStreamReader, BufferedReader, PrintWriter}

class Sender private(val ip: String, val port: Int, val messages: Event[String]) extends EventSource[String]
with Breakable {
  val sender = SenderActor.start()
  messages.hook {
    occ => sender ! occ.value
  }

  private object SenderActor extends Actor {
    def act {
			loop {
				receive {
	        case message: String => sendToSocket(message)
	      }
			}
    }

    def sendToSocket(message: String) {
      dangerous {
        val socket = new java.net.Socket(ip, port)
        val out = new PrintWriter(socket.getOutputStream, true)
        val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

        out.println(message)
        val reply = in.readLine()
        Sender.this.occur(reply)

        out.close()
        in.close()
        socket.close()
      }
    }
  }

}

object Sender {
  def apply(ip: String, port: Int, messages: Event[String]): Sender = {
    new Sender(ip, port, messages)
  }
}