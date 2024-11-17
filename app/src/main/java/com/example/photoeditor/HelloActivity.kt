package com.example.photoeditor

import com.example.photoeditor.databinding.ActivityHelloBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HelloActivity : AppCompatActivity() {
    lateinit var binding: ActivityHelloBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)
        binding = ActivityHelloBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sighinFragment = SighinFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.card, sighinFragment)
            .addToBackStack(null)
            .commit()
    }
}