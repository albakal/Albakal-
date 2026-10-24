package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OrderAlarmManager(private val context: Context) {
  private var alertJob: Job? = null
  private var isAlerting = false
  private var toneGenerator: ToneGenerator? = null

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  fun startAlert() {
    if (isAlerting) return
    isAlerting = true

    alertJob = CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
      } catch (_: Exception) {
        try {
          toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        } catch (_: Exception) {}
      }

      while (isActive && isAlerting) {
        try {
          toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 350)
          vibrateDevice()
        } catch (_: Exception) {}
        delay(900)
      }
    }
  }

  fun stopAlert() {
    isAlerting = false
    alertJob?.cancel()
    alertJob = null
    try {
      toneGenerator?.stopTone()
      toneGenerator?.release()
      toneGenerator = null
    } catch (_: Exception) {}
  }

  private fun vibrateDevice() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(300)
      }
    } catch (_: Exception) {}
  }
}
