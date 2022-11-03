package com.template.ea

import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.view.Display
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService


class MainActivity : AppCompatActivity() {
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_main)
//  }

  // https://github.com/googlearchive/android-AccelerometerPlay/blob/master/app/src/main/java/com/example/android/accelerometerplay/AccelerometerPlayActivity.java

  private lateinit var simulationView: SimulationView
  private lateinit var sensorManager: SensorManager
  private lateinit var powerManager: PowerManager
  private lateinit var wManager: WindowManager
  private lateinit var disp: Display
  private lateinit var wakeLock: WakeLock

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    sensorManager = getSystemService()!!
    powerManager = getSystemService()!!
    wManager = getSystemService()!!
    disp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      this.display!!
    } else {
      @Suppress("DEPRECATION")
      wManager.defaultDisplay
    }

    wakeLock = powerManager.newWakeLock(
      PowerManager.SCREEN_BRIGHT_WAKE_LOCK, javaClass.name
    )

    simulationView = SimulationView(this).apply {
      setBackgroundResource(R.drawable.wood)
    }
    setContentView(simulationView)
  }

  override fun onResume() {
    super.onResume()
    simulationView.startSimulation(sensorManager, wManager, disp)
  }

  override fun onPause() {
    super.onPause()
    simulationView.stopSimulation()
    if (wakeLock.isHeld) {
      wakeLock.release()
    }
  }

}