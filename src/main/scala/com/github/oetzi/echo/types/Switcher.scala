import com.github.oetzi.echo.core._
import com.github.oetzi.echo.Echo._

package com.github.oetzi.echo.types {
	class Switcher[T](behaviour : Behaviour[T], event : EventSource[Behaviour[T]]) extends Behaviour[T](
		Switcher.construct(behaviour, event)) { }
		
	object Switcher {
		private def construct[T](behaviour : Behaviour[T], event : EventSource[Behaviour[T]]) : Time => T = {
			{ time =>
				val result = event.occAt(time).getOrElse {
					val before = event.occsBefore(time)
					
					if (!before.isEmpty) {
						before.last
					}
				
					else {
						new Occurrence(0, behaviour)
					}
				}
				
				result.value.at(time)
			}
		} 
	}
}