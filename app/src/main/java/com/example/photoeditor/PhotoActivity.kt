package com.example.photoeditor

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

        // Получение объекта Photo из Intent
        val photo = intent.getSerializableExtra("photo") as Photo
        // Отображение изображения
        if (photo.original) {
            binding.photoView.setImageResource(photo.path.toInt())
        } else {
            Picasso.get()
                .load(photo.path)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.photoView)
        }
        // Установка даты
        binding.textViewCreatedAt.text = photo.createdAt.toString()
    }
}