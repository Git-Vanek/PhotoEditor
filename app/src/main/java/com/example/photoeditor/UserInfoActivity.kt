package com.example.photoeditor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.ActivityUserInfoBinding
import java.time.LocalDate

class UserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Установка Toolbar как ActionBar
        setSupportActionBar(binding.toolbar)

        // Создание данных пользователя
        val user = User("123", "Vanya Rocker", "vanyaOK@gmail.com", "123", LocalDate.parse("2017-12-12"))

        // Отображение данных пользователя
        binding.textViewUsername.text = user.username
        binding.textViewEmail.text = user.email
        binding.textViewCreatedAt.text = user.createdAt.toString()

        // Установка обработчика нажатия для кнопки "Назад"
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Установка обработчика нажатия для кнопки выхода
        binding.buttonLogout.setOnClickListener {
            logout()
        }
    }

    // Функция для обработки возврата
    private fun back() {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(this, MainActivity::class.java)
        // Переход на MainActivity
        startActivity(intent)
    }

    // Функция для обработки выхода
    private fun logout() {
        // Создание Intent для перехода на HelloActivity
        val intent = Intent(this, HelloActivity::class.java)
        // Переход на HelloActivity
        startActivity(intent)
    }
}
