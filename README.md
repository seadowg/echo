# echo

## Mission Statement

'echo' is a functional reactive programming framework that I am creating for my undergraduate project. Scala was
chosen as the implementation because: its nice, it allows for proper FP and it also has some nice features for writing
DSLs (implicit functions etc). How is this going work? What are you asking me for?

## The Basic Spec

Im wanting echo to work a bit like this (requires knowledge of FRP to understand):

* Pure Behaviours - time varying continuous values that are essentially a function with respect to time. By this I mean that a Behaviours value is lazily calculated and is done so recursively (not updated at an interval).
* Pureish Events that work in the way that Haskellers define them but also work as collections (ie have each, map, filter functions)
* Graph like model where as much of the control flow is modelled in one stream as possible (less threading)
* Alternative 'Signal'-esque based system that is implemented using Behaviours and Events. Signals are used by some FRP frameworks to combine the two types into one - it has a continuous value and discrete changes. I would like to experiment with this principal.
* A focus on actual practical industrial usage. This means implementing Behaviours and Events to abstract networking, IO components etc. The graphical stuff is already done to death so it doesn't interest me as much.
* An additional focus on the framework being as DSLie as possible. I want to put a lot of thought into how naturally and intuitively echo can be used.

## So... what can it do?

At the moment? Not a lot. This is about the coolest thing you can do so far:

    val event = new Event[Boolean]
    val behaviour = new Behaviour(time => "Hello!") until (event, time => "DIE!")
    println behaviour.now // => Hello!
    event.occur(true)
    println behaviour.now // => DIE!
    
This will create a Behaviour thats value will be "Hello!" until the Event 'event' occurs (its 'occur' function is called).
The Event and Behaviour implementations are also kind of weak - no division, no Event operations and Behaviour operations must
be performed on Behaviours with EXACTLY the same type. However, I will try to keep this readme as up to date as possible with
the current state of the project.

In the meantime, the progress should be fairly evident from reading the specs [here](http://www.github.com/oetzi/echo/wiki).