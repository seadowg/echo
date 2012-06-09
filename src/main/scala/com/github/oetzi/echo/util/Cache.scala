package com.github.oetzi.echo.util

class Cache[T, U](work: T => U) {
  private var lastVal: U = null.asInstanceOf[U]
  private var lastCheck: T = null.asInstanceOf[T]
  private var checkedBefore = false
  
  
  def get(check: T): U = {
    if (!checkedBefore || !check.equals(lastCheck)) {
      checkedBefore = true
      
      lastVal = work(check)
      lastCheck = check
      lastVal
    }
    
    else {
      lastVal
    }
  }
}