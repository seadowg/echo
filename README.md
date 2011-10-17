# echo

## Mission Statement

'echo' is a functional reactive programming framework that I am creating for my undergraduate project. Scala was
chosen as the implementation because: its nice, it allows for proper FP and it also has some nice features for writing
DSLs (implicit functions etc). How is this going work? What are you asking me for?

## The Basic Spec

I'm wanting echo to work a bit like this (requires knowledge of FRP to understand):

* Pure Behaviours - time varying continuous values that are essentially a function with respect to time. By this I mean 
that a Behaviours value is lazily calculated and is done so recursively (not updated at an interval).
* Pureish Events that work in the way that Haskellers define them but also work as collections (ie have each, map, filter 
functions)
* Graph like model where as much of the control flow is modelled in one stream as possible (less threading)
* Alternative 'Signal'-esque based system that is implemented using Behaviours and Events. Signals are used by some FRP 
frameworks to combine the two types into one - it has a continuous value and discrete changes. I would like to experiment 
with this principal.
* A focus on actual practical industrial usage. This means implementing Behaviours and Events to abstract networking, IO 
components etc. The graphical stuff is already done to death so it doesn't interest me as much.
* An additional focus on the framework being as DSLie as possible. I want to put a lot of thought into how naturally and 
intuitively echo can be used.

## Building

The project uses [buildr](http://buildr.apache.org/]) for building. If you haven't used this before it is a ruby tool for 
building JVM languages. To build the project simply do:

    buildr
    
This will compile the project into `target/classes` and run all tests. You can also package echo as a jar by doing:

    buildr package
    
There are a few other tasks added to buildfile:

* `examples` - builds and packages the project and then builds the examples in '/examples'
* `typeset` - this will typeset my dissertation that surrounds the project
* `console` - this builds everything then fires up a scala console with the built classes included in the classpath

## Including

To include echo in a project make sure the .jar is in the classpath for your project and import it like so:

    import com.github.oetzi.echo.Echo._
    import com.github.oetzi.echo._
    
You need to include `Echo._` as it contains the implicit functions etc needed for echo's DSL elements (such as combining Behaviours with values).    

## So... what can it do?

At the moment? Not a lot. These are about the coolest things you can do so far:

    val event = new Event[Boolean]
    val behaviour = new Behaviour(time => "Hello!") until (event, time => "DIE!")
    println behaviour.now // => Hello!
    event.occur(true)
    println behaviour.now // => DIE!
    
This will create a Behaviour thats value will be "Hello!" until the Event 'event' occurs (its 'occur' function is called).
The Event and Behaviour implementations are also kind of weak - no division, no Event operations and Behaviour operations 
must be performed on Behaviours with EXACTLY the same type. However, I will try to keep this readme as up to date as 
possible with the current state of the project.

In the meantime, the progress should be fairly evident from reading the specs [here](http://www.github.com/oetzi/echo/wiki).