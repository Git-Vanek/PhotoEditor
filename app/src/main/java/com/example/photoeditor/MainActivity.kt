package com.example.photoeditor

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.example.photoeditor.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val MY_SETTINGS: String = "my_settings"
    private lateinit var photoList: List<Photo>
    private lateinit var photoAdapter: PhotoAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Настройка SearchView
        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterList(newText)
                }
                return true
            }
        })
        val sp: SharedPreferences = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE)
        val rv: RecyclerView = binding.recyclerView
        photoList = buildPhotoList()
        photoAdapter = PhotoAdapter(photoList)
        rv.adapter = photoAdapter
        rv.layoutManager = GridLayoutManager(this, 3)
        binding.buttonUserInfo.setOnClickListener {
            userInfo()
        }
        binding.buttonSettings.setOnClickListener {
            settings()
        }
        binding.buttonAdd.setOnClickListener {
            add()
        }
        binding.buttonLoad.setOnClickListener {
            load()
        }
        binding.buttonDelete.setOnClickListener {
            delete()
        }
    }

    private fun filterList(query: String) {
        val filteredList = photoList.filter { Photo ->
            Photo.name.contains(query, ignoreCase = true)
        }
        photoAdapter.filterList(filteredList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPhotoList(): List<Photo> {
        val list= mutableListOf(
            Photo("1", "1", "1", true, R.drawable.im_1.toString(), LocalDate.parse("2018-12-12")),
            Photo("2", "2", "2", true, R.drawable.im_2.toString(), LocalDate.parse("2019-12-12")),
            Photo("3", "3", "3", true, R.drawable.im_3.toString(), LocalDate.parse("2020-12-12")),
            Photo("4", "4", "4", false, "https://i.pinimg.com/originals/3a/dd/56/3add569b9c10105fbda36232e6abb706.jpg", LocalDate.parse("2021-12-12")),
            Photo("5", "5", "5", false, "https://i.pinimg.com/originals/2f/dd/a6/2fdda6a89ec49eea3a9818fa20705785.jpg", LocalDate.parse("2022-12-12")),
            Photo("6", "6", "6", false, "https://i.pinimg.com/originals/5d/e2/42/5de24294bad21ec99931f4c362354f22.jpg", LocalDate.parse("2023-12-12")),
        )
        return list
    }

    private fun userInfo() {

    }

    private fun settings() {

    }

    private fun add() {

    }

    private fun load() {

    }

    private fun delete() {

    }
}