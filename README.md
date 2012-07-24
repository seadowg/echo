# echo

## Using

If you're just wanting to use echo to build something there is a rather informal tutorial and a documentation for
the various types and functions [here](http://www.seadowg.com/echo). The remainder of this document will mainly deal 
with working on the echo source code.

## Building

The echo source can be built, tested etc using the standard Scala tool [sbt](http://www.scala-sbt.org/). Once in 
the echo directory you can simply run the following command to fire it up:

   sbt
   
Information and tutorials on using sbt can be found [here](http://code.google.com/p/simple-build-tool/wiki/RunningSbt).

## Hacking

Please feel free to hack on the echo source: [Fork](http://help.github.com/fork-a-repo/) it, hack it then make a [pull request](http://help.github.com/send-pull-requests/).

### Packages

The echo framework currently has five packages:

* root - contains EchoApp and Echo class.
* core - contains implementation of core FRP types and operations
* ui - simple UI framework classes
* io - currently contains network classes and code to deal with exceptions
* util - package for any extra code used mainly for DRYness internally

### Testing

Testing for echo is written using Specs. The tests are contained in src/test/scala and are part of the
`core.test` package so they can access package level functions of FRP types. Tests can be run using the sbt command
`test`.
    
Remember: tests are good. You should write them.

### FRP Block

Much of the code in echo is implemented with respect to the original semantics of FRP. This means
that many of the operations are restricted so they can only be executed during the 'setup' phase
of an Echo application (see tutorial [here](http://www.seadowg.com/echo)) as otherwise they exhibit
unusual behaviour. Any member functions of FRP types or functions that operate on FRP types should most likely
have their code executed in an FRP block like so:

    frp {
      //code
    }
    
This allows you to guarantee that you code will only ever execute on empty Events at time 0. Of course
you may be able to argue that their are use cases for an operation outside of these restrictions and if
so fair enough: argue it!