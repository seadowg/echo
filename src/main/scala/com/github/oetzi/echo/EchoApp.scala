package com.github.oetzi.echo

import com.github.oetzi.echo.Control._
import com.github.oetzi.echo.Echo._

abstract class EchoApp {
	def setup(args : Array[String])
	
	def main(args : Array[String]) {
		writeLock.acquire()
		createLock.acquire()
		
		freezeTime(now()) {
			() => setup(args)
		}
		
		writeLock.release()
		createLock.release()
	}
}