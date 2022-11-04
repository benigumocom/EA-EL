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
    val games = mutableListOf<String>()

    countries.indices.apply {
      forEach { left ->
        forEach { right ->
          if (left < right) {
            games.add("${countries[left]}\tvs\t${countries[right]}")
          }
        }
      }
    }

//    countries.forEachIndexed { left, leftCountry ->
//      countries.forEachIndexed { right, rightCountry ->
//        if (left < right) {
//          games.add("$leftCountry vs $rightCountry")
//        }
//      }
//    }


    games.forEachIndexed() { index, game ->
      println("${index + 1}. $game")
    }
    println("All ${games.size} games")

    assertTrue(
      countries.size * (countries.size - 1) / 2 == games.size
    )

  }
}