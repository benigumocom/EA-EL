package com.template.ea

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun salesman() {

    val countries = listOf("brazil", "italy", "england", "spain", "japan")
    val indices = countries.indices
    indices.forEach { left ->
      indices.forEach { right ->
        if (left < right) {
          println("${countries[left]}\tvs\t${countries[right]}")
        }
      }
    }

  }
}