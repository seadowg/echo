import java.util.Date

package com.github.oetzi.echo {
	class Behaviour[T](rule : Double => T) {
		def now() : T = {
			rule(new Date().getTime)
		}
	}
}