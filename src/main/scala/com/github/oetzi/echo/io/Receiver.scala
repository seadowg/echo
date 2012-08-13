package com.github.oetzi.echo.io

import java.lang.Thread
import java.net.ServerSocket
import com.github.oetzi.echo.core.{Behaviour, EventSource}
import java.io.{PrintWriter, InputStreamReader, BufferedReader}
import actors.Actor._

/**Represents a network input. Network requests received occur
 * as part of this Event.
 */
class Receiver private(val port: Int, val reply: String => Behaviour[String]) extends EventSource[String]
with Breakable {
  private val thread = new Thread(new Runnable() {
    def run() {
      dangerous {
        val socket = new ServerSocket(port)

        while (true) {
          val request = socket.accept()
          
          actor {
            val in = new BufferedReader(new InputStreamReader(request.getInputStream))
            val out = new PrintWriter(request.getOutputStream, true)

            val message = in.readLine()
            Receiver.this.occur(message)
            out.println(reply(message).eval())

            out.close()
            in.close()
            request.close()
          }
        }

        socket.close()
      }
    }
  })
  thread.start()
}

object Receiver {
  /**Create a receiver that listens on the specified port
   * and replies to requests with the value of the Behaviour[String]
   * produced by the reply function (with each incoming message as
   * input).
   */
  def apply(port: Int)(reply: String => Behaviour[String]): Receiver = {
    new Receiver(port, reply)
  }
}