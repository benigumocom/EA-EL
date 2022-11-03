package com.template.ea

import android.content.Context
import android.graphics.Canvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface.ROTATION_0
import android.view.Surface.ROTATION_180
import android.view.Surface.ROTATION_270
import android.view.Surface.ROTATION_90
import android.widget.FrameLayout
import androidx.core.content.getSystemService

class MainView(
  context: Context
) : FrameLayout(context), SensorEventListener {

  private lateinit var accelerometer: Sensor
  private var sensorX = 0f
  private var sensorY = 0f

  private val ballW: Int
  private val ballH: Int
  private val pX: Float
  private val pY: Float
  private var centerX = 0f
  private var centerY = 0f
  private var boundX = 0f
  private var boundY = 0f

  private val ballSystem: BallSystem

  private val sensorManager = context.getSystemService<SensorManager>()!!

  fun start() {
    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
  }

  fun stop() {
    sensorManager.unregisterListener(this)
  }

  init {

    val metrics = resources.displayMetrics
    pX = metrics.xdpi / 0.0254f
    pY = metrics.ydpi / 0.0254f

    // rescale the ball so it's about 0.5 cm on screen
    ballW = (DIAMETER * pX + 0.5f).toInt()
    ballH = (DIAMETER * pY + 0.5f).toInt()

    ballSystem = BallSystem(this, ballW, ballH)
//    val opts = BitmapFactory.Options()
//    opts.inDither = true
//    opts.inPreferredConfig = Bitmap.Config.RGB_565
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    centerX = (w - ballW) / 2f
    centerY = (h - ballH) / 2f
    boundX = (w / pX - DIAMETER) / 2f
    boundY = (h / pY - DIAMETER) / 2f
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
        sensorY =  event.values[0]
      }
      ROTATION_180 -> {
        sensorX = -event.values[0]
        sensorY = -event.values[1]
      }
      ROTATION_270 -> {
        sensorX =  event.values[1]
        sensorY = -event.values[0]
      }
    }
  }

  override fun onDraw(canvas: Canvas?) = with (ballSystem) {
    update(sensorX, sensorY, System.currentTimeMillis(), boundX, boundY)

    balls.forEach { ball ->
      ball.apply {
        translationX = centerX + posX * pX
        translationY = centerY - posY * pY
      }
    }

    invalidate()
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

  companion object {
    const val DIAMETER = 0.004f
  }

}
