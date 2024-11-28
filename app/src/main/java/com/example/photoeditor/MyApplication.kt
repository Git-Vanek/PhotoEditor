package com.example.photoeditor

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Установка темы при запуске приложения
        setAppTheme()
    }

    private fun setAppTheme() {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedTheme = sharedPreferences.getString("theme", "Системная")
        when (savedTheme) {
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}