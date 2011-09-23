package com.github.oetzi.echo {
	object Echo {
		implicit def val2Behaviour[T](value : T) : Behaviour[T] = new Behaviour(time => value)
	}
}