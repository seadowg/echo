package com.github.oetzi.echo.core

import com.github.oetzi.echo.Echo._

class Behavior[T](private val rule: Time => T) {
  def eval() : T = {
    this.at(now())
  }

  private[echo] def at(time: Time): T = {
    rule(time)
  }

  def until[A](event: Event[A], behavior: Behavior[T]): Behavior[T] = {
    val rule: Time => T = {
      time =>
        val occ = event.top(time)
        
        if (occ == None) {
          this.at(time)
        }
    
        else {
          behavior.at(time)
        }
    }
         
    new Behavior(rule)
  }

  def until[A](after: Time, event: Event[A], behaviour: Behavior[T]): Behavior[T] = {
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

    new Behavior(rule)
  }
  
  def toggle[A](event : Event[A], behaviour : Behavior[T]) : Behavior[T] = {
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

    new Behavior(rule)
  }

  def map[B](func: T => B): Behavior[B] = {
    new Behavior(time => func(this.at(time)))
  }

  def map2[U, V](behaviour: Behavior[U])(func: (T, U) => V): Behavior[V] = {
    new Behavior(time => func(this.at(time), behaviour.at(time)))
  }

  def map3[U, V, W](beh1: Behavior[U], beh2: Behavior[V])(func: (T, U, V) => W): Behavior[W] = {
    new Behavior(time => func(this.at(time), beh1.at(time), beh2.at(time)))
  }
}

object Behavior {
  def apply[T](rule: Time => T): Behavior[T] = {
    new Behavior(rule)
  }
}