package com.example.bustracking.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

/**
 * Supported App Languages
 */
enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English (Default)"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ (Kannada)")
}

/**
 * Centralized App Language Manager
 * Manages persistent language preference and notifies Compose UI reactively on change.
 */
object AppLanguageManager {
    private const val PREFS_NAME = "bus_tracking_language_prefs"
    private const val KEY_SELECTED_LANGUAGE = "selected_language_code"

    private val _currentLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val currentLanguage: State<AppLanguage> = _currentLanguage

    fun initialize(context: Context) {
        val prefs = getPreferences(context)
        val savedCode = prefs.getString(KEY_SELECTED_LANGUAGE, AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code
        val lang = if (savedCode == AppLanguage.KANNADA.code) AppLanguage.KANNADA else AppLanguage.ENGLISH
        _currentLanguage.value = lang
        applyLocaleToContext(context, lang)
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        _currentLanguage.value = language
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_SELECTED_LANGUAGE, language.code).apply()
        applyLocaleToContext(context, language)
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Suppress("DEPRECATION")
    private fun applyLocaleToContext(context: Context, language: AppLanguage) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
