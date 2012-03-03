package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._

class Behaviour[T](private val rule: Time => T) {
  def eval() : T = {
    this.at(now())
  }

  private[echo] def at(time: Time): T = {
    rule(time)
  }

  def transform(func: Time => Time): Behaviour[T] = {
    new Behaviour(time => this.at(func(time)))
  }

  def until[A](event: Event[A], behaviour: Behaviour[T]): Behaviour[T] = {
    val rule: Time => T = {
      time =>
        val occ = event.top(time)
        
        if (occ == None) {
          this.at(time)
        }
    
        else {
          behaviour.at(time)
        }
    }
         
    new Behaviour(rule)
  }

  def until[A](after: Time, event: Event[A], behaviour: Behaviour[T]): Behaviour[T] = {
    val rule : Time => T = {
      time =>
        val occ = event.top(time)
        
        if (occ == None || occ.get.time < after) {
          this.at(time)
        }

        else {
          behaviour.at(time)
        }
    }

    new Behaviour(rule)
  }
  
  def toggle[A](event : Event[A], behaviour : Behaviour[T]) : Behaviour[T] = {
    val rule: Time => T = {
      time => 
        val occ = event.top(time)
        
        if (occ == None || occ.get.num % 2 == 0) {
          this.at(time)
        }

        else {
          behaviour.at(time)
        }
    }

    new Behaviour(rule)
  }

  def map[B](func: T => B): Behaviour[B] = {
    new Behaviour(time => func(this.at(time)))
  }

  def map2[U, V](behaviour: Behaviour[U])(func: (T, U) => V): Behaviour[V] = {
    new Behaviour(time => func(this.at(time), behaviour.at(time)))
  }

  def map3[U, V, W](beh1: Behaviour[U], beh2: Behaviour[V])(func: (T, U, V) => W): Behaviour[W] = {
    new Behaviour(time => func(this.at(time), beh1.at(time), beh2.at(time)))
  }
}

object Behaviour {
  def apply[T](rule: Time => T): Behaviour[T] = {
    new Behaviour(rule)
  }
}