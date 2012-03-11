package com.github.oetzi.echo.io

import java.lang.Thread
import java.net.ServerSocket
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.core.{Behavior, EventSource}
import java.io.{PrintWriter, InputStreamReader, BufferedReader}


class Receiver private (val port: Int, val reply : String => Behavior[String]) extends EventSource[String]
  with Breakable {
  private var running = true

  private val thread = new Thread(new Runnable() {
    def run() {
      dangerous {
        () =>
          val socket = new ServerSocket(port)

          while (Receiver.this.running) {
            val request = socket.accept()
            val in = new BufferedReader(new InputStreamReader(request.getInputStream))
            val out = new PrintWriter(request.getOutputStream(), true)

            val message = in.readLine()
            Receiver.this.occur(message)
            out.println(reply(message).eval())

            out.close()
            in.close()
            request.close()
          }

          socket.close()
      }
    }
  })
  thread.start()

  protected[echo] def die() {
    this.running = false
  }
}

object Receiver {
  def apply(port : Int)(reply : String => Behavior[String]) : Receiver = {
    new Receiver(port, reply)  
  }
}