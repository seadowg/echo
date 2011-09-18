# echo

## Mission Statement

'echo' is a functional reactive programming framework that I am creating for my undergraduate project. Scala was
chosen as the implementation because: its nice, it allows for FP and it will hopefully make this framework available to
Java also. How is this going work? What are you asking me for?

## So FRP? What can I do?

At the moment? Not a lot. This is about the coolest thing you can do so far:

    val event = new Event[Boolean]
    val behaviour = new Behaviour(time => "Hello!") until (event, time => "DIE!")
    
This will create a Behaviour thats value will be "Hello!" until the Event 'event' occurs (its 'occur' function is called).
The Event and Behaviour implementations are also kind of weak - no division, no Event operations and Behaviour operations must
be performed on Behaviours with EXACTLY the same type. However, I will try to keep this readme as up to date as possible with
the current state of the project.

In the meantime, the progress should be fairly evident from reading the specs [here](http://www.github.com/oetzi/echo/wiki).