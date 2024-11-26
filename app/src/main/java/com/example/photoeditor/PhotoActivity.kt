package com.example.photoeditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.ActivityPhotoBinding

class PhotoActivity : AppCompatActivity() {
    // Инициализация переменной для View Binding
    private lateinit var binding: ActivityPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка темы для активности
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        // Установка макета активности
        setContentView(binding.root)

        // Получение объекта Photo из Intent
        @Suppress("DEPRECATION") val photo = intent.getSerializableExtra("photo") as Photo

        // Создание экземпляра фрагмента ViewPhotoFragment с передачей переменной photo
        val viewPhotoFragment = ViewPhotoFragment.newInstance(photo)

        // Начало транзакции фрагмента
        supportFragmentManager.beginTransaction()
            // Замена текущего фрагмента на ViewPhotoFragment
            .replace(R.id.imageContent, viewPhotoFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }
}