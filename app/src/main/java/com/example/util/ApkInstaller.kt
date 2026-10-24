package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ApkInstaller {
  /**
   * Attempts to launch the package installer for the compiled APK,
   * or share the APK file.
   */
  fun installOrShareApk(context: Context) {
    try {
      // Look for the built APK in build outputs or filesDir
      val possibleApkPaths = listOf(
        File("/app/build/outputs/apk/debug/app-debug.apk"),
        File(context.filesDir, "app-debug.apk")
      )

      val apkFile = possibleApkPaths.firstOrNull { it.exists() }
      if (apkFile != null) {
        val apkUri: Uri = FileProvider.getUriForFile(
          context,
          "${context.packageName}.fileprovider",
          apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
          setDataAndType(apkUri, "application/vnd.android.package-archive")
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(installIntent)
      } else {
        // Direct download APK file link
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/albakal/Albakal-/raw/main/apk/albakal.apk")).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(webIntent)
      }
    } catch (_: Exception) {
      try {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/albakal/Albakal-/raw/main/apk/albakal.apk")).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(webIntent)
      } catch (_: Exception) {}
    }
  }
}
