package com.example.photoeditor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.photoeditor.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    val MY_SETTINGS: String = "my_settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        var sp: SharedPreferences =getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE)


    }
}