package com.template.ea

import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.math.pow
import kotlin.math.sqrt

class BallSystem(view: PlayView, width: Int, height: Int) {

  private var last = 0L
  val balls = List(NUM_BALLS) { Ball(view.context) }

  init {
    balls.forEach { ball ->
      ball.apply {
        setBackgroundResource(R.drawable.ball)
        setLayerType(FrameLayout.LAYER_TYPE_HARDWARE, null)
      }
      view.addView(ball, ViewGroup.LayoutParams(width, height))
    }
  }

  private fun updatePositions(sx: Float, sy: Float, current: Long) {
    if (last != 0L) {
      val delta = (current - last).toFloat() / 1000f
      balls.forEach { ball ->
        ball.compute(sx, sy, delta)
      }
    }
    last = current
  }

  fun update(sx: Float, sy: Float, now: Long, horizontalBound: Float, verticalBound: Float) {

    updatePositions(sx, sy, now)

    val maxIterations = 10

    var more = true
    val count = balls.size
    var k = 0
    while (k < maxIterations && more) {
      more = false
      for (i in 0 until count) {
        val curr = balls[i]
        for (j in i + 1 until count) {
          val ball = balls[j]
          var dx = ball.posX - curr.posX
          var dy = ball.posY - curr.posY
          var dd = dx * dx + dy * dy
          if (dd <= PlayView.DIAMETER.pow(2)) {
            dx += (Math.random().toFloat() - 0.5f) * 0.0001f
            dy += (Math.random().toFloat() - 0.5f) * 0.0001f
            dd = dx * dx + dy * dy
            val d = sqrt(dd.toDouble()).toFloat()
            val c = 0.5f * (PlayView.DIAMETER - d) / d
            val effectX = dx * c
            val effectY = dy * c
            curr.posX -= effectX
            curr.posY -= effectY
            ball.posX += effectX
            ball.posY += effectY
            more = true
          }
        }
        curr.limit(horizontalBound, verticalBound)
      }
      k++
    }
  }

  companion object {
    const val NUM_BALLS = 5
  }
}