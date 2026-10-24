package com.example.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageUtils {
  /**
   * Copies an image from a selected content Uri into the app's internal storage
   * and returns the persistent local file path as a string.
   */
  fun copyUriToInternalStorage(context: Context, uri: Uri, prefix: String = "img"): String? {
    return try {
      val uploadDir = File(context.filesDir, "uploads").apply {
        if (!exists()) mkdirs()
      }
      val fileName = "${prefix}_${UUID.randomUUID()}.jpg"
      val destFile = File(uploadDir, fileName)

      context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(destFile).use { output ->
          input.copyTo(output)
        }
      }
      destFile.absolutePath
    } catch (_: Exception) {
      null
    }
  }

  fun saveImageToInternalStorage(context: Context, uri: Uri, prefix: String = "img"): String? {
    return copyUriToInternalStorage(context, uri, prefix)
  }
}
