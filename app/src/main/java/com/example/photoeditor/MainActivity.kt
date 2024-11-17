package com.example.photoeditor

import android.content.Context
import android.content.SharedPreferences
import com.example.photoeditor.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val MY_SETTINGS: String = "my_settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sp: SharedPreferences = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE)
        // Настройка RecyclerView

        binding.buttonAdd.setOnClickListener {
            add()
        }
        binding.buttonDelete.setOnClickListener {
            delete()
        }
    }

    private fun add() {

    }

    private fun delete() {

    }
}