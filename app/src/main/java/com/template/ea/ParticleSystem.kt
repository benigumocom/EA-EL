package com.template.ea

import android.view.ViewGroup
import android.widget.FrameLayout

class ParticleSystem(
  view: SimulationView,
  mDstWidth: Int,
  mDstHeight: Int
) {

  private var mLastT = 0L

  val mBalls = arrayOfNulls<Particle>(NUM_PARTICLES)

  init {
    val context = view.context
    for (i in mBalls.indices) {
      mBalls[i] = Particle(context)
      mBalls[i]?.setBackgroundResource(R.drawable.ball)
      mBalls[i]?.setLayerType(FrameLayout.LAYER_TYPE_HARDWARE, null)
      view.addView(mBalls[i], ViewGroup.LayoutParams(mDstWidth, mDstHeight))
    }
  }

  private fun updatePositions(sx: Float, sy: Float, timestamp: Long) {
    //println("$timestamp $sx, $sy") OK
    if (mLastT != 0L) {
      val dT = (timestamp - mLastT).toFloat() / 1000f
      val count = mBalls.size
      for (i in 0 until count) {
        val ball = mBalls[i]
        ball!!.computePhysics(sx, sy, dT)
      }
    }
    mLastT = timestamp
  }

  fun update(
    sx: Float,
    sy: Float,
    now: Long,
    mHorizontalBound: Float,
    mVerticalBound: Float
  ) {

    updatePositions(sx, sy, now)

    val NUM_MAX_ITERATIONS = 10

    var more = true
    val count = mBalls.size
    var k = 0
    while (k < NUM_MAX_ITERATIONS && more) {
      more = false
      for (i in 0 until count) {
        val curr = mBalls[i]
        for (j in i + 1 until count) {
          val ball = mBalls[j]
          var dx = ball!!.posX - curr!!.posX
          var dy = ball.posY - curr.posY
          var dd = dx * dx + dy * dy
          if (dd <= SimulationView.sBallDiameter2) {
            dx += (Math.random().toFloat() - 0.5f) * 0.0001f
            dy += (Math.random().toFloat() - 0.5f) * 0.0001f
            dd = dx * dx + dy * dy
            val d = Math.sqrt(dd.toDouble()).toFloat()
            val c = 0.5f * (SimulationView.sBallDiameter - d) / d
            val effectX = dx * c
            val effectY = dy * c
            curr.posX -= effectX
            curr.posY -= effectY
            ball.posX += effectX
            ball.posY += effectY
            more = true
          }
        }
        curr!!.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound)
      }
      k++
    }
  }

  fun getParticleCount(): Int {
    return mBalls.size
  }

  fun getPosX(i: Int): Float {
    return mBalls[i]!!.posX
  }

  fun getPosY(i: Int): Float {
    return mBalls[i]!!.posY
  }

  companion object {
    const val NUM_PARTICLES = 5
  }
}