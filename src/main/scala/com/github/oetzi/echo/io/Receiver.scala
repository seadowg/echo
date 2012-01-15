package com.github.oetzi.echo.io

import java.lang.Thread
import java.net.ServerSocket
import com.github.oetzi.echo.core.{Occurrence, Event, EventSource}
import com.github.oetzi.echo.Echo._
import java.io.{InputStreamReader, BufferedReader}


class Receiver(val port: Int) extends EventSource[String] {
  val errors: Event[Exception] = Event[Exception]

  new Thread(new Runnable() {
    def run() {
      try {
        val socket = new ServerSocket(port)

        while (true) {
          val request = socket.accept()
          val in = new BufferedReader(new InputStreamReader(request.getInputStream()))
          Receiver.this.occur(new Occurrence(now, in.readLine()))
          in.close()
          request.close()
        }
      }

      catch {
        case e: Exception => errors.occur(new Occurrence(now, e))
      }
    }
  }).start()
}