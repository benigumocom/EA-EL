package com.template.ea

import android.content.Context
import android.view.View

class Particle(context: Context) : View(context) {

  var posX = Math.random().toFloat()
  var posY = Math.random().toFloat()
  private var velX = 0f
  private var velY = 0f

  fun computePhysics(sx: Float, sy: Float, dT: Float) {
    val ax = -sx / 5
    val ay = -sy / 5
    posX += velX * dT + ax * dT * dT / 2
    posY += velY * dT + ay * dT * dT / 2
    velX += ax * dT
    velY += ay * dT
  }

  fun resolveCollisionWithBounds(boundX: Float, boundY: Float) {
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