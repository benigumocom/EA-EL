package com.template.ea

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

// https://github.com/googlearchive/android-AccelerometerPlay/blob/master/app/src/main/java/com/example/android/accelerometerplay/AccelerometerPlayActivity.java

class MainActivity : AppCompatActivity() {

  private lateinit var playView: MainView

  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowInsetsControllerCompat(window, window.decorView)
      .hide(WindowInsetsCompat.Type.systemBars())
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
