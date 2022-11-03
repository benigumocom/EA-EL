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
import android.view.Surface.ROTATION_0
import android.view.Surface.ROTATION_180
import android.view.Surface.ROTATION_270
import android.view.Surface.ROTATION_90
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.getSystemService

class MainView(
  context: Context
) : FrameLayout(context), SensorEventListener {

  private val dstWidth: Int
  private val dstHeight: Int
  private lateinit var accelerometer: Sensor
  private val xDpi: Float
  private val yDpi: Float
  private val metersToPixelsX: Float
  private val metersToPixelsY: Float
  private var xOrigin = 0f
  private var yOrigin = 0f
  private var sensorX = 0f
  private var sensorY = 0f
  private var horizontalBound = 0f
  private var verticalBound = 0f
  private val ballSystem: BallSystem

  private val sensorManager = context.getSystemService<SensorManager>()!!
  private val windowManager = context.getSystemService<WindowManager>()!!

  fun start() {
    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
  }

  fun stop() {
    sensorManager.unregisterListener(this)
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


    xDpi = 393F //metrics.xdpi
    yDpi = 800F //metrics.ydpi
    metersToPixelsX = xDpi / 0.0254f
    metersToPixelsY = yDpi / 0.0254f

    // rescale the ball so it's about 0.5 cm on screen
    dstWidth = (DIAMETER * metersToPixelsX + 0.5f).toInt()
    dstHeight = (DIAMETER * metersToPixelsY + 0.5f).toInt()
    ballSystem = BallSystem(this, dstWidth, dstHeight)//, mHorizontalBound, mVerticalBound)
    val opts = BitmapFactory.Options()
    opts.inDither = true
    opts.inPreferredConfig = Bitmap.Config.RGB_565
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    xOrigin = (w - dstWidth) * 0.5f
    yOrigin = (h - dstHeight) * 0.5f
    horizontalBound = (w / metersToPixelsX - DIAMETER) * 0.5f
    verticalBound = (h / metersToPixelsY - DIAMETER) * 0.5f
  }

  override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
    when (display?.rotation) {
      ROTATION_0 -> {
        sensorX = event.values[0]
        sensorY = event.values[1]
      }
      ROTATION_90 -> {
        sensorX = -event.values[1]
        sensorY = event.values[0]
      }
      ROTATION_180 -> {
        sensorX = -event.values[0]
        sensorY = -event.values[1]
      }
      ROTATION_270 -> {
        sensorX = event.values[1]
        sensorY = -event.values[0]
      }
    }
  }

  override fun onDraw(canvas: Canvas?) = with (ballSystem) {
    update(sensorX, sensorY, System.currentTimeMillis(), horizontalBound, verticalBound)
    balls.forEach { ball ->
      ball.apply {
        translationX = xOrigin + posX * metersToPixelsX
        translationY = yOrigin - posY * metersToPixelsY
      }
    }
    invalidate()
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

  companion object {
    const val DIAMETER = 0.004f
  }

}
