package com.example.photoeditor

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.ActivityPhotoBinding
import com.squareup.picasso.Picasso

class PhotoActivity : AppCompatActivity() {
    // Инициализация переменной для View Binding
    private lateinit var binding: ActivityPhotoBinding

    // Ключ для SharedPreferences
    private val MY_SETTINGS: String = "my_settings"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка темы для активности
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        // Установка макета активности
        setContentView(binding.root)
        // Установка Toolbar как ActionBar
        setSupportActionBar(binding.toolbar)

        // Установка обработчика нажатия для кнопки "Назад"
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Получение объекта Photo из Intent
        val photo = intent.getSerializableExtra("photo") as Photo
        // Установка даты
        binding.textViewCreatedAt.text = photo.createdAt.toString()

        // Создание экземпляра фрагмента ViewPhotoFragment с передачей переменной photo
        val viewPhotoFragment = ViewPhotoFragment.newInstance(photo)

        // Начало транзакции фрагмента
        supportFragmentManager.beginTransaction()
            // Замена текущего фрагмента на ViewPhotoFragment
            .replace(R.id.content, viewPhotoFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }

    // Функция для обработки возврата
    private fun back() {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(this, MainActivity::class.java)
        // Переход на MainActivity
        startActivity(intent)
    }
}