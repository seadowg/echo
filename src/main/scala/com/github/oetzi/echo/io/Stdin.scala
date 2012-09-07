package com.github.oetzi.echo.io

import scala.io.Source

import java.lang.Thread

import com.github.oetzi.echo.core.EventSource

object Stdin extends EventSource[String] {
  private val thread = new Thread(new Runnable() {
    def run() {
      Source.stdin.getLines.foreach(occur(_))
    }
  })
  thread.start()
}
