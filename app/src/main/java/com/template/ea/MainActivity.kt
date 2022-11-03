package com.template.ea

import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

// https://github.com/googlearchive/android-AccelerometerPlay/blob/master/app/src/main/java/com/example/android/accelerometerplay/AccelerometerPlayActivity.java

class MainActivity : AppCompatActivity() {

  private lateinit var playView: MainView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    playView = MainView(this).apply {
      setBackgroundResource(R.drawable.wood)
    }
    setContentView(playView)
  }

  override fun onResume() {
    super.onResume()
    playView.start()
  }

  override fun onPause() {
    playView.stop()
    super.onPause()
  }

}
