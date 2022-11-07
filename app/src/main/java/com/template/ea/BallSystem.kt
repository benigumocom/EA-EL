package com.template.ea

import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.math.pow
import kotlin.math.sqrt

class BallSystem(view: MainView, width: Int, height: Int) {

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

  fun update(sx: Float, sy: Float, now: Long, boundX: Float, boundY: Float) {

    updatePositions(sx, sy, now)

    var k = 0
    var more = true
    val indices = balls.indices

    while (k < ITERATIONS && more) {
      more = false

      indices.forEach { a ->
        val ballA = balls[a]
        indices.forEach { b ->
          if (a < b) {

            // 【当たり判定】円と円の当たり
            //  https://yttm-work.jp/collision/collision_0002.html
            val ballB = balls[b]
            var dx = ballB.posX - ballA.posX
            var dy = ballB.posY - ballA.posY
            var d2 = dx.pow(2) + dy.pow(2)

            if (d2 <= MainView.DIAMETER.pow(2)) { // collision
              dx += Math.random().toFloat() / 2 / 10000
              dy += Math.random().toFloat() / 2 / 10000
              d2 = dx.pow(2) + dy.pow(2)

              val d = sqrt(d2.toDouble()).toFloat()
              val c = (MainView.DIAMETER - d) / d / 2f
              val effectX = dx * c
              val effectY = dy * c

              ballA.posX -= effectX
              ballA.posY -= effectY
              ballB.posX += effectX
              ballB.posY += effectY

              more = true
            }

          }
        }
        ballA.bound(boundX, boundY)
      }
      k++
    }

  }

  companion object {
    private const val NUM_BALLS = 10
    private const val ITERATIONS = 10
  }
}