package com.example.photoeditor

import com.example.photoeditor.databinding.ActivityHelloBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HelloActivity : AppCompatActivity() {
    // Инициализация переменной для View Binding
    lateinit var binding: ActivityHelloBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка темы для активности
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityHelloBinding.inflate(layoutInflater)
        // Установка макета активности
        setContentView(binding.root)

        // Создание экземпляра фрагмента AutorisationFragment с передачей переменной photo
        val sighinFragment = SighinFragment.newInstance("", "")
        // Начало транзакции фрагмента
        supportFragmentManager.beginTransaction()
            // Замена текущего фрагмента на SighinFragment
            .replace(R.id.card, sighinFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }
}