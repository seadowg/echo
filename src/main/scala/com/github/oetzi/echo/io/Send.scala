package com.github.oetzi.echo.io

import actors.Actor
import com.github.oetzi.echo.core.EventSource
import com.github.oetzi.echo.Echo._
import java.io.{InputStreamReader, BufferedReader, PrintWriter}

class Send private (val ip: String, val port: Int, message : String) extends EventSource[String]
  with Breakable {
  val sender = SenderActor.start()
  sender ! message

  private object SenderActor extends Actor {
    def act {
      receive {
        case message: String => sendToSocket(message)
      }
    }

    def sendToSocket(message: String) {
      dangerous {
        () =>
          val socket = new java.net.Socket(ip, port)
          val out = new PrintWriter(socket.getOutputStream, true)
          val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
          
          out.println(message)
          val reply = in.readLine()
          Send.this.occur(now(), reply)
          
          out.close()
          in.close()
          socket.close()
          this.exit()
      }
    }
  }
}

object Send {
  def apply(ip : String, port : Int, message : String) : Send = {
    new Send(ip, port, message)
  }
}