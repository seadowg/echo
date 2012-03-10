package com.github.oetzi.echo.types

import com.github.oetzi.echo.core._
import com.github.oetzi.echo.Echo._
import com.github.oetzi.echo.Control._

class Switcher[T](behaviour: Behavior[T], val event: Event[Behavior[T]]) extends Behavior[T](
  Switcher.construct(behaviour, event)) {
}

object Switcher {
  private def construct[T](initial: Behavior[T], event: Event[Behavior[T]]): Time => T = {
		frp {
			() =>
    		{
		      time =>
		        val occ = event.top(time)

		        if (occ == None) {
		           initial.at(time)
		        }

		        else {
		          occ.get.value.at(time)
		        }
		    }
		}
  }
}