package com.example.photoeditor

import android.content.pm.PackageManager
import com.example.photoeditor.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    // Инициализация переменной для View Binding
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
        lateinit var mainFragment: MainFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка темы для активности
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)
        // Инициализация View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Создание экземпляра фрагмента MainFragment
        mainFragment = MainFragment()
        // Установка макета активности
        setContentView(binding.root)
        // Проверка разрешений
        checkPermissions()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, mainFragment)
                .commit()
        }
        // Начало транзакции фрагмента
        supportFragmentManager.beginTransaction()
            // Замена текущего фрагмента на MainFragment
            .replace(R.id.mainContent, mainFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }

    // Метод для проверки разрешений
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
        }
    }

    // Обработка результата запроса разрешений
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.permission_to_write),
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}