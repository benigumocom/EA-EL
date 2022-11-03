package com.template.ea

import android.content.Context
import android.view.View

class Ball(context: Context) : View(context) {

  var posX = Math.random().toFloat()
  var posY = Math.random().toFloat()
  private var velX = 0f
  private var velY = 0f

  fun compute(sx: Float, sy: Float, time: Float) {
    val ax = -sx / 5
    val ay = -sy / 5
    posX += velX * time + ax * time * time / 2
    posY += velY * time + ay * time * time / 2
    velX += ax * time
    velY += ay * time
  }

  fun limit(boundX: Float, boundY: Float) {
    val x = posX
    val y = posY
    if (x > boundX) {
      posX = boundX
      velX = 0f
    } else if (x < -boundX) {
      posX = -boundX
      velX = 0f
    }
    if (y > boundY) {
      posY = boundY
      velY = 0f
    } else if (y < -boundY) {
      posY = -boundY
      velY = 0f
    }
  }
}