package com.example.photoeditor

import android.content.Context
import android.content.SharedPreferences
import com.example.photoeditor.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    // Инициализация переменной для View Binding
    private lateinit var binding: ActivityMainBinding

    // Ключ для SharedPreferences
    private val MY_SETTINGS: String = "my_settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка темы для активности
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Установка макета активности
        setContentView(binding.root)

        // Получение экземпляра SharedPreferences
        val sp: SharedPreferences = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE)
    }
}