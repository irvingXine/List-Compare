package com.example.myapplicationexample.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppTheme {
    AMOLED, MATERIAL
}

class PreferenceManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    private val _theme = MutableStateFlow(loadTheme())
    val theme: StateFlow<AppTheme> = _theme

    private fun loadTheme(): AppTheme {
        val themeName = sharedPreferences.getString("theme", AppTheme.AMOLED.name)
        return try {
            AppTheme.valueOf(themeName ?: AppTheme.AMOLED.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.AMOLED
        }
    }

    fun setTheme(theme: AppTheme) {
        sharedPreferences.edit().putString("theme", theme.name).apply()
        _theme.value = theme
    }
}
