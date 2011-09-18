package com.github.oetzi.echo {
	class Event[T] {
		var edges : List[T => Any] = List[T => Any]()
		
		def occur(event : T) {
			edges.foreach(edge => edge(event))
		}
		
		def each(edge : T => Any) {
			edges = edges ++ List[T => Any](edge)
		}
	}
}