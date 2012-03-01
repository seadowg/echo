package com.github.oetzi.echo.io

import java.lang.Thread
import java.net.ServerSocket
import com.github.oetzi.echo.core.{Occurrence, EventSource}
import com.github.oetzi.echo.Echo._
import java.io.{InputStreamReader, BufferedReader}


class Receiver(val port: Int) extends EventSource[String] with Breakable {
  private var running = true

  private val thread = new Thread(new Runnable() {
    def run() {
      dangerous {
        () =>
          val socket = new ServerSocket(port)

          while (Receiver.this.running) {
            val request = socket.accept()
            val in = new BufferedReader(new InputStreamReader(request.getInputStream))
            Receiver.this.occur(now, in.readLine())
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