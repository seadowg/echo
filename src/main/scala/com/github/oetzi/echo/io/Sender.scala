package com.github.oetzi.echo.io

import actors.Actor
import com.github.oetzi.echo.core._
import com.github.oetzi.echo.Control._
import java.io.{InputStreamReader, BufferedReader, PrintWriter}

/**Represents an output network connection. Sends each occurrence value from the specified
 * Event and occurs when messages are replies to.
 */
class Sender private(val ip: String, val port: Int, val messages: Event[String]) extends EventSource[String]
with Breakable {
  val sender = SenderActor.start()
  messages.hook {
    occ => sender ! occ.value
  }

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
  /**Create a new Sender that sends messages to the given IP and port
   * whenever the given Event occurs.
   */
  def apply(ip: String, port: Int, messages: Event[String]): Sender = {
    frp {
      new Sender(ip, port, messages)
    }
  }
}