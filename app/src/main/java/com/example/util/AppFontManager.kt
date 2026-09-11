package com.example.util

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import java.io.File
import java.io.FileOutputStream

/**
 * Manages loading, validation and storage of custom TTF/OTF fonts
 * Persists chosen font across app restarts in internal storage
 */
object AppFontManager {

  private const val CUSTOM_FONT_FILE_NAME = "custom_user_font.ttf"
  private const val PREFS_NAME = "app_font_prefs"
  private const val KEY_CUSTOM_FONT_NAME = "custom_font_name"
  private const val KEY_IS_CUSTOM_FONT = "is_custom_font"

  /**
   * Reads, validates and saves a custom font from a file Uri (TTF or OTF)
   * Returns Result.success(displayName) or Result.failure(Exception)
   */
  fun validateAndSaveCustomFont(context: Context, uri: Uri): Result<String> {
    return try {
      val contentResolver = context.contentResolver
      val mimeType = contentResolver.getType(uri) ?: ""

      // 1. Copy to temp file for validation
      val tempFile = File(context.cacheDir, "temp_font_validation.bin")
      contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(tempFile).use { output ->
          input.copyTo(output)
        }
      } ?: return Result.failure(Exception("امکان خواندن فایل از حافظه گوشی وجود ندارد."))

      // 2. Validate file size (a valid font is usually at least a few KB)
      if (tempFile.length() < 1024) {
        tempFile.delete()
        return Result.failure(Exception("حجم فایل فونت بسیار کم است و نامعتبر به نظر می‌رسد."))
      }

      // 3. Validate font by attempting to create an Android Typeface
      val typeface: Typeface? = try {
        Typeface.createFromFile(tempFile)
      } catch (e: Exception) {
        null
      }

      if (typeface == null) {
        tempFile.delete()
        return Result.failure(Exception("قالب فایل نامعتبر است. لطفاً یک فایل سالم با پسوند TTF یا OTF انتخاب کنید."))
      }

      // 4. Move to permanent internal storage
      val permanentFontFile = File(context.filesDir, CUSTOM_FONT_FILE_NAME)
      if (permanentFontFile.exists()) {
        permanentFontFile.delete()
      }
      tempFile.copyTo(permanentFontFile, overwrite = true)
      tempFile.delete()

      // Extract a nice readable name
      val extractedName = uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.')
        ?: "فونت سفارشی"

      // Save preferences
      context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_CUSTOM_FONT_NAME, extractedName)
        .putBoolean(KEY_IS_CUSTOM_FONT, true)
        .apply()

      Result.success(extractedName)
    } catch (e: Exception) {
      Result.failure(Exception("خطا در پردازش فایل قلم: ${e.localizedMessage ?: "فرمت نامعتبر"}"))
    }
  }

  /**
   * Retrieves the saved custom FontFamily if valid and exists
   */
  fun getCustomFontFamily(context: Context): FontFamily? {
    val file = File(context.filesDir, CUSTOM_FONT_FILE_NAME)
    if (!file.exists() || file.length() < 1024) {
      return null
    }
    return try {
      // Validate typeface loadable
      val tf = Typeface.createFromFile(file)
      if (tf != null) {
        FontFamily(
          Font(file, FontWeight.Normal),
          Font(file, FontWeight.Medium),
          Font(file, FontWeight.Bold),
          Font(file, FontWeight.ExtraBold),
          Font(file, FontWeight.SemiBold)
        )
      } else {
        null
      }
    } catch (_: Exception) {
      null
    }
  }

  /**
   * Returns whether a valid custom font is currently stored
   */
  fun hasCustomFont(context: Context): Boolean {
    val file = File(context.filesDir, CUSTOM_FONT_FILE_NAME)
    return file.exists() && file.length() >= 1024
  }

  /**
   * Returns the display name of the saved custom font
   */
  fun getCustomFontDisplayName(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_CUSTOM_FONT_NAME, "فونت سفارشی کاربری") ?: "فونت سفارشی کاربری"
  }

  /**
   * Deletes the custom font and resets preferences
   */
  fun removeCustomFont(context: Context) {
    val file = File(context.filesDir, CUSTOM_FONT_FILE_NAME)
    if (file.exists()) {
      file.delete()
    }
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      .edit()
      .remove(KEY_CUSTOM_FONT_NAME)
      .putBoolean(KEY_IS_CUSTOM_FONT, false)
      .apply()
  }
}
