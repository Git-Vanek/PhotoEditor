package com.example.photoeditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.ActivityPhotoBinding

class PhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение объекта Photo из Intent
        val photo = intent.getSerializableExtra("photo") as Photo

    }
}