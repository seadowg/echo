package com.github.oetzi.echo.util.test

import org.specs._
import com.github.oetzi.echo.util.Cache

object CacheSpec extends Specification {
  "Cache" should {
    "have a get function" >> {
      "that returns the executed work for different check values" in {
        val cache = new Cache[Int, Int](ch => ch)
        
        cache.get(1) mustEqual 1
        cache.get(2) mustEqual 2
      }
      
      "that returns a cached value if check is the same as the last one" in {
        var value = 1
        val cache = new Cache[Int, Int](ch => value)
        
        cache.get(1) mustEqual 1
        value = 2
        cache.get(1) mustEqual 1
      }
    }
  }
}