package com.github.oetzi.echo

abstract class EchoApp {
	def setup(args : Array[String])
	
	def main(args : Array[String]) {
		setup(args)
	}
}