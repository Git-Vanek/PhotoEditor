package com.example.photoeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import com.example.photoeditor.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    // Инициализация переменной для View Binding
    private lateinit var binding: ActivityMainBinding

    // Ключ для SharedPreferences
    private val MY_SETTINGS: String = "my_settings"
    private lateinit var sharedPreferences: SharedPreferences

    // Список фотографий
    private lateinit var photoList: MutableList<Photo>

    // Адаптер для RecyclerView
    private lateinit var photoAdapter: PhotoAdapter

    companion object {
        private const val REQUEST_CODE_PICK_PHOTO = 1
        private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка темы для активности
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Установка макета активности
        setContentView(binding.root)
        // Установка Toolbar как ActionBar
        setSupportActionBar(binding.toolbar)

        checkPermissions()

        // Настройка SearchView
        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Логика для обработки отправки запроса
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Логика для фильтрации списка при изменении текста
                if (newText != null) {
                    filterList(newText)
                }
                return true
            }
        })

        // Получение экземпляра SharedPreferences
        sharedPreferences = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE)

        // Инициализация RecyclerView
        val rv: RecyclerView = binding.recyclerView
        photoList = buildPhotoList()
        photoAdapter = PhotoAdapter(photoList) { photo ->
            openPhotoActivity(photo)
        }
        rv.adapter = photoAdapter
        rv.layoutManager = GridLayoutManager(this, 3) // 3 элемента в строке

        // Установка обработчиков нажатия для кнопок
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

    // Метод для фильтрации списка фотографий
    private fun filterList(query: String) {
        val filteredList = photoList.filter { photo ->
            photo.createdAt.toString().contains(query, ignoreCase = true)
        }
        photoAdapter.filterList(filteredList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    // Метод для создания списка фотографий
    private fun buildPhotoList(): MutableList<Photo> {
        val list = mutableListOf(
            Photo("1", "1", true, "/storage/emulated/0/Pictures/Screenshots/im_1.jpg", LocalDate.parse("2018-12-12")),
            Photo("2", "2", true, "/storage/emulated/0/Pictures/Screenshots/im_2.jpg", LocalDate.parse("2019-12-12")),
            Photo("3", "3", true, "/storage/emulated/0/Pictures/Screenshots/im_3.jpg", LocalDate.parse("2020-12-12")),
            Photo(
                "4",
                "4",
                false,
                "https://i.pinimg.com/originals/3a/dd/56/3add569b9c10105fbda36232e6abb706.jpg",
                LocalDate.parse("2021-12-12")
            ),
            Photo(
                "5",
                "5",
                false,
                "https://i.pinimg.com/originals/2f/dd/a6/2fdda6a89ec49eea3a9818fa20705785.jpg",
                LocalDate.parse("2022-12-12")
            ),
            Photo(
                "6",
                "6",
                false,
                "https://i.pinimg.com/originals/5d/e2/42/5de24294bad21ec99931f4c362354f22.jpg",
                LocalDate.parse("2023-12-12")
            ),
        )
        return list
    }

    // Метод для отображения информации пользователя
    private fun userInfo() {
        // Создание Intent для перехода на UserInfoActivity
        val intent = Intent(this, UserInfoActivity::class.java)
        // Запуск UserInfoActivity
        startActivity(intent)
    }

    // Метод для отображения настроек
    private fun settings() {
        // Создание Intent для перехода на SettingsActivity
        val intent = Intent(this, SettingsActivity::class.java)
        // Запуск SettingsActivity
        startActivity(intent)
    }

    // Метод для перехода на редактирование элемента
    private fun openPhotoActivity(photo: Photo) {
        val intent = Intent(this, PhotoActivity::class.java)
        intent.putExtra("photo", photo)
        startActivity(intent)
    }

    // Метод для добавления элемента
    @RequiresApi(Build.VERSION_CODES.O)
    private fun add() {
        // Создаем диалоговое окно для выбора источника фотографии
        val builder = MaterialAlertDialogBuilder(this, R.style.Widget_PhotoEditor_AlertDialog)
        builder.setTitle("Выберите источник фотографии")
        builder.setItems(arrayOf("С устройства", "С интернета")) { dialog, which ->
            when (which) {
                0 -> pickPhotoFromDevice()
                1 -> pickPhotoFromInternet()
            }
        }
        builder.show()
    }

    // Метод получения фотографии с устройства
    private fun pickPhotoFromDevice() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO)
    }

    // Обработка результата выбора фотографии с устройства
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                photoList.add(Photo("7", "7", true, selectedImageUri.toString(), LocalDate.now()))
                photoAdapter.notifyDataSetChanged()
            }
        }
    }

    // Метод получения фотографии из интернета
    @RequiresApi(Build.VERSION_CODES.O)
    private fun pickPhotoFromInternet() {
        // В этом примере мы будем использовать простой EditText для ввода URL
        val builder = MaterialAlertDialogBuilder(this, R.style.Widget_PhotoEditor_AlertDialog)
        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_URI
        builder.setTitle("Введите URL фотографии")
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            val url = input.text.toString()
            photoList.add(Photo("8", "8", false, url, LocalDate.now()))
        }
        builder.setNegativeButton("Отмена") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    // Метод для загрузки элемента
    private fun load() {
        val selectedItems = photoAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(applicationContext, "Ни одна фотография не выбрана", Toast.LENGTH_LONG).show()
        } else {
            for (selectedItem in selectedItems) {
                if (!selectedItem.original) {
                    // Сохранение фотографии на устройство
                    saveImageToDevice(selectedItem)
                }
                else (
                    Toast.makeText(applicationContext, "Фотография уже на вашем устройстве.", Toast.LENGTH_LONG).show()
                )
            }
        }
    }

    // Метод для сохранения изображения на устройство
    private fun saveImageToDevice(selectedItem: Photo) {
        val context = this
        val target = object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
                    try {
                        val outputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        Toast.makeText(context, "Изображение сохранено: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(context, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Toast.makeText(context, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                // Ничего не делаем
            }
        }

        Picasso.get().load(selectedItem.path).into(target)
    }

    // Метод для удаления элемента
    private fun delete() {
        val selectedItems = photoAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(applicationContext, "Ни одна фотография не выбрана", Toast.LENGTH_LONG).show()
        } else {
            MaterialAlertDialogBuilder(this, R.style.Widget_PhotoEditor_AlertDialog)
                .setTitle("Подтверждение удаления")
                .setMessage("Вы действительно хотите удалить выбранные фотографии?")
                .setPositiveButton("Да") { _, _ ->
                    photoAdapter.filterList(photoAdapter.dataset.filter { it !in selectedItems })
                    photoAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("Нет", null)
                .show()
        }
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
                Toast.makeText(applicationContext, "Разрешение на запись в память устройства не предоставлено", Toast.LENGTH_LONG).show()
            }
        }
    }
}