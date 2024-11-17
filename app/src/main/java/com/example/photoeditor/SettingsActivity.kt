package com.example.photoeditor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    // Ключ для SharedPreferences
    private val MY_SETTINGS: String = "my_settings"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Установка Toolbar как ActionBar
        setSupportActionBar(binding.toolbar)

        // Инициализация SharedPreferences
        sharedPreferences = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE)

        // Установка адаптеров для Spinner
        val imageFormatOptions = arrayOf("PNG", "JPG")
        val imageFormatAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, imageFormatOptions)
        imageFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerImageFormat.adapter = imageFormatAdapter

        // Установка обработчика нажатия для кнопки "Назад"
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Установка обработчика нажатия для кнопки "Сохранить"
        binding.buttonSave.setOnClickListener {
            save()
        }
    }

    // Функция для обработки возврата
    private fun back() {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(this, MainActivity::class.java)
        // Переход на MainActivity
        startActivity(intent)
    }

    private fun save() {
        // Получение выбранных значений из Spinner
        val selectedImageFormat = binding.spinnerImageFormat.selectedItem.toString()

        // Сохранение значений в SharedPreferences
        with(sharedPreferences.edit()) {
            putString("image_format", selectedImageFormat)
            apply()
        }

        // Подтверждение сохранения
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
    }
}
