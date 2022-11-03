package com.template.ea

import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService


class MainActivity : AppCompatActivity() {
  // https://github.com/googlearchive/android-AccelerometerPlay/blob/master/app/src/main/java/com/example/android/accelerometerplay/AccelerometerPlayActivity.java

  private lateinit var playView: PlayView
  private lateinit var wakeLock: WakeLock

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    wakeLock = getSystemService<PowerManager>()!!.newWakeLock(
      PowerManager.SCREEN_BRIGHT_WAKE_LOCK, javaClass.name
    )

    playView = PlayView(this).apply {
      setBackgroundResource(R.drawable.wood)
    }
    setContentView(playView)
  }

  override fun onResume() {
    super.onResume()
    playView.start()
  }

  override fun onPause() {
    super.onPause()
    playView.stop()
    if (wakeLock.isHeld) {
      wakeLock.release()
    }
  }

}