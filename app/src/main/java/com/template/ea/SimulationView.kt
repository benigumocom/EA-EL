package com.template.ea

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.DisplayMetrics
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import android.widget.FrameLayout

class SimulationView(
  context: Context
) : FrameLayout(context), SensorEventListener {

  private val mDstWidth: Int
  private val mDstHeight: Int
  private lateinit var mAccelerometer: Sensor
  //private var mLastT: Long = 0
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

  private var mSensorManager: SensorManager? = null
  private var mDisplay: Display? = null
  private var mWindowManager: WindowManager? = null

  fun startSimulation(sensorManager: SensorManager, windowManager: WindowManager,display: Display) {

    println(">>>>>>>>>>>>")

    mSensorManager = sensorManager
    mWindowManager = windowManager
    mDisplay = display

    mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
  }

  fun stopSimulation() {
    println("<<<<<<<<<<<")

    mSensorManager?.unregisterListener(this)

    mSensorManager = null
    mWindowManager = null
    mDisplay = null
  }

  init {
    //mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    //val metrics = DisplayMetrics()
    //mWindowManager?.defaultDisplay?.getMetrics(metrics)

    val metrics = DisplayMetrics()

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
      @Suppress("DEPRECATION")
      display?.getRealMetrics(metrics)
    } else {
      @Suppress("DEPRECATION")
      display.getMetrics(metrics)
    }


    mXDpi = 393F //metrics.xdpi
    mYDpi = 800F //metrics.ydpi
    mMetersToPixelsX = mXDpi / 0.0254f
    mMetersToPixelsY = mYDpi / 0.0254f

    // rescale the ball so it's about 0.5 cm on screen
    mDstWidth = (sBallDiameter * mMetersToPixelsX + 0.5f).toInt()
    mDstHeight = (sBallDiameter * mMetersToPixelsY + 0.5f).toInt()
    mParticleSystem = ParticleSystem(this, mDstWidth, mDstHeight)//, mHorizontalBound, mVerticalBound)
    val opts = BitmapFactory.Options()
    opts.inDither = true
    opts.inPreferredConfig = Bitmap.Config.RGB_565
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    mXOrigin = (w - mDstWidth) * 0.5f
    mYOrigin = (h - mDstHeight) * 0.5f
    mHorizontalBound = (w / mMetersToPixelsX - sBallDiameter) * 0.5f
    mVerticalBound = (h / mMetersToPixelsY - sBallDiameter) * 0.5f
  }

  override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
    when (mDisplay?.rotation) {
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
    //println("@@@@ ${mSensorX}, $mSensorY")
  }

  override fun onDraw(canvas: Canvas?) {
    val particleSystem = mParticleSystem
    val now = System.currentTimeMillis()
    val sx = mSensorX
    val sy = mSensorY
    //particleSystem.update(sx, sy, now)
    particleSystem.update(sx, sy, now, mHorizontalBound, mVerticalBound)
    val xc = mXOrigin
    val yc = mYOrigin
    val xs = mMetersToPixelsX
    val ys = mMetersToPixelsY
    val count = particleSystem.getParticleCount()
    for (i in 0 until count) {
      val x = xc + particleSystem.getPosX(i) * xs
      val y = yc - particleSystem.getPosY(i) * ys
      //println("@@@ $i $x, $y")
      particleSystem.mBalls[i]?.translationX = x
      particleSystem.mBalls[i]?.translationY = y
      //println("@@@ $x, $y")
    }
    invalidate()
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

  companion object {
    const val sBallDiameter = 0.004f
    const val sBallDiameter2 = sBallDiameter * sBallDiameter
  }
}