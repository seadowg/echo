package com.github.oetzi.echo

import com.github.oetzi.echo.Control._

abstract class EchoApp {
	def setup(args : Array[String])
	
	def main(args : Array[String]) {
		writeLock.acquire()
		setup(args)
		writeLock.release()
	}
}