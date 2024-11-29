package com.example.photoeditor

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {
    // Ключ для SharedPreferences
    private val settings: String = "my_settings"
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        // Установка темы при запуске приложения
        setAppTheme()
    }

    private fun setAppTheme() {
        sharedPreferences = getSharedPreferences(settings, Context.MODE_PRIVATE)
        val savedTheme = sharedPreferences.getString("theme", "System")
        when (savedTheme) {
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}