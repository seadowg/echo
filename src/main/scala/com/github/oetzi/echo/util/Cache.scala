package com.github.oetzi.echo.util

/** A proxy for non wasteful evaluation of a pure function
 *  that is evaluated monotonically.
 */
class Cache[T, U](work: T => U) {
  private var lastVal: U = null.asInstanceOf[U]
  private var lastCheck: T = null.asInstanceOf[T]
  private var checkedBefore = false
  
  
  /** Evaluates and returns work with respect to check or returns the cached
   * value if check is the same as used in the last evaluation.
   */ 
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