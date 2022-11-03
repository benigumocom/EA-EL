package com.template.ea

import android.R
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_main)
//  }

  private var mSimulationView: SimulationView? = null
  private var mSensorManager: SensorManager? = null
  private var mPowerManager: PowerManager? = null
  private var mWindowManager: WindowManager? = null
  private var mDisplay: Display? = null
  private var mWakeLock: WakeLock? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    mPowerManager = getSystemService(POWER_SERVICE) as PowerManager
    mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    mDisplay = mWindowManager!!.getDefaultDisplay()

    mWakeLock = mPowerManager!!.newWakeLock(
      PowerManager.SCREEN_BRIGHT_WAKE_LOCK, javaClass.name
    )

    mSimulationView = SimulationView(this).apply {
      setBackgroundResource(R.drawable.wood)
    }
    setContentView(mSimulationView)
  }

  override fun onResume() {
    super.onResume()
    mSimulationView!!.startSimulation()
  }

  override fun onPause() {
    super.onPause()
    mSimulationView!!.stopSimulation()
    mWakeLock!!.release()
  }

  class SimulationView(context: Context?) : FrameLayout(context!!),
    SensorEventListener {
    private val mDstWidth: Int
    private val mDstHeight: Int
    private val mAccelerometer: Sensor
    private var mLastT: Long = 0
    private val mXDpi: Float
    private val mYDpi: Float
    private val mMetersToPixelsX: Float
    private val mMetersToPixelsY: Float
    private var mXOrigin = 0f
    private var mYOrigin = 0f
    private var mSensorX = 0f
    private var mSensorY = 0f
    private var mHorizontalBound = 0f
    private var mVerticalBound = 0f
    private val mParticleSystem: ParticleSystem

    class Particle : View {
      var mPosX = Math.random().toFloat()
      var mPosY = Math.random().toFloat()
      private var mVelX = 0f
      private var mVelY = 0f

      constructor(context: Context?) : super(context) {}
      constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
      constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
      ) {
      }

      constructor(
        context: Context?, attrs: AttributeSet?, defStyleAttr: Int,
        defStyleRes: Int
      ) : super(context, attrs, defStyleAttr, defStyleRes) {
      }

      fun computePhysics(sx: Float, sy: Float, dT: Float) {
        val ax = -sx / 5
        val ay = -sy / 5
        mPosX += mVelX * dT + ax * dT * dT / 2
        mPosY += mVelY * dT + ay * dT * dT / 2
        mVelX += ax * dT
        mVelY += ay * dT
      }

          fun resolveCollisionWithBounds() {
        val xmax = mHorizontalBound
        val ymax = mVerticalBound
        val x = mPosX
        val y = mPosY
        if (x > xmax) {
          mPosX = xmax
          mVelX = 0f
        } else if (x < -xmax) {
          mPosX = -xmax
          mVelX = 0f
        }
        if (y > ymax) {
          mPosY = ymax
          mVelY = 0f
        } else if (y < -ymax) {
          mPosY = -ymax
          mVelY = 0f
        }
      }
    }

    class ParticleSystem {
      val mBalls = arrayOfNulls<Particle>(Companion.NUM_PARTICLES)

      init {
        for (i in mBalls.indices) {
          mBalls[i] = Particle(context)
          mBalls[i].setBackgroundResource(R.drawable.ball)
          mBalls[i].setLayerType(LAYER_TYPE_HARDWARE, null)
          addView(mBalls[i], ViewGroup.LayoutParams(mDstWidth, mDstHeight))
        }
      }

      private fun updatePositions(sx: Float, sy: Float, timestamp: Long) {
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

      fun update(sx: Float, sy: Float, now: Long) {

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
              var dx = ball!!.mPosX - curr!!.mPosX
              var dy = ball.mPosY - curr.mPosY
              var dd = dx * dx + dy * dy
              if (dd <= sBallDiameter2) {
                dx += (Math.random().toFloat() - 0.5f) * 0.0001f
                dy += (Math.random().toFloat() - 0.5f) * 0.0001f
                dd = dx * dx + dy * dy
                val d = Math.sqrt(dd.toDouble()).toFloat()
                val c = 0.5f * (sBallDiameter - d) / d
                val effectX = dx * c
                val effectY = dy * c
                curr.mPosX -= effectX
                curr.mPosY -= effectY
                ball.mPosX += effectX
                ball.mPosY += effectY
                more = true
              }
            }
            curr!!.resolveCollisionWithBounds()
          }
          k++
        }
      }

      fun getParticleCount(): Int {
        return mBalls.field
      }

      fun getPosX(i: Int): Float {
        return mBalls[i]!!.mPosX
      }

      fun getPosY(i: Int): Float {
        return mBalls[i]!!.mPosY
      }

      companion object {
        const val NUM_PARTICLES = 5
      }
    }

    fun startSimulation() {
      mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stopSimulation() {
      mSensorManager.unregisterListener(this)
    }

    init {
      mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
      val metrics = DisplayMetrics()
      getWindowManager().getDefaultDisplay().getMetrics(metrics)
      mXDpi = metrics.xdpi
      mYDpi = metrics.ydpi
      mMetersToPixelsX = mXDpi / 0.0254f
      mMetersToPixelsY = mYDpi / 0.0254f

      // rescale the ball so it's about 0.5 cm on screen
      mDstWidth = (sBallDiameter * mMetersToPixelsX + 0.5f).toInt()
      mDstHeight = (sBallDiameter * mMetersToPixelsY + 0.5f).toInt()
      mParticleSystem = ParticleSystem()
      val opts = BitmapFactory.Options()
      opts.inDither = true
      opts.inPreferredConfig = Bitmap.Config.RGB_565
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
      // compute the origin of the screen relative to the origin of
      // the bitmap
      mXOrigin = (w - mDstWidth) * 0.5f
      mYOrigin = (h - mDstHeight) * 0.5f
      mHorizontalBound = (w / mMetersToPixelsX - sBallDiameter) * 0.5f
      mVerticalBound = (h / mMetersToPixelsY - sBallDiameter) * 0.5f
    }

    override fun onSensorChanged(event: SensorEvent) {
      if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
      when (mDisplay.getRotation()) {
        Surface.ROTATION_0 -> {
          mSensorX = event.values[0]
          mSensorY = event.values[1]
        }
        Surface.ROTATION_90 -> {
          mSensorX = -event.values[1]
          mSensorY = event.values[0]
        }
        Surface.ROTATION_180 -> {
          mSensorX = -event.values[0]
          mSensorY = -event.values[1]
        }
        Surface.ROTATION_270 -> {
          mSensorX = event.values[1]
          mSensorY = -event.values[0]
        }
      }
    }

    override fun onDraw(canvas: Canvas?) {
      val particleSystem = mParticleSystem
      val now = System.currentTimeMillis()
      val sx = mSensorX
      val sy = mSensorY
      particleSystem.update(sx, sy, now)
      val xc = mXOrigin
      val yc = mYOrigin
      val xs = mMetersToPixelsX
      val ys = mMetersToPixelsY
      val count = particleSystem.getParticleCount()
      for (i in 0 until count) {
        val x = xc + particleSystem.getPosX(i) * xs
        val y = yc - particleSystem.getPosY(i) * ys
        particleSystem.mBalls[i].setTranslationX(x)
        particleSystem.mBalls[i].setTranslationY(y)
      }
      invalidate()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
      private const val sBallDiameter = 0.004f
      private const val sBallDiameter2 = sBallDiameter * sBallDiameter
    }
  }

}