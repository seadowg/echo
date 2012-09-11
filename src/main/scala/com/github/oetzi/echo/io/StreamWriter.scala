package com.github.oetzi.echo.io

import java.io.PrintStream

import com.github.oetzi.echo.core.Event

class StreamWriter(val stream: PrintStream, val output: Event[String]) {
  output.hook(occ => stream.println(occ.value))
}

object StreamWriter {
  def apply(stream: PrintStream, output: Event[String]) = new StreamWriter(stream, output)
}

case class Stdout private(out: Event[String]) extends StreamWriter(Console.out, out)

case class Stderr private(err: Event[String]) extends StreamWriter(Console.err, err)
