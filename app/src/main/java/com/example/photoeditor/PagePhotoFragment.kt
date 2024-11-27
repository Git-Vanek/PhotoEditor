package com.example.photoeditor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.databinding.FragmentPagePhotoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate

@Suppress("DEPRECATION")
class PagePhotoFragment(list: MutableList<Photo>) : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentPagePhotoBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Список фотографий
    private var photoList: MutableList<Photo> = list

    // Адаптер для RecyclerView
    private lateinit var photoAdapter: PhotoAdapter

    companion object {
        private const val REQUEST_CODE_PICK_PHOTO = 1
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация View Binding для макета фрагмента
        _binding = FragmentPagePhotoBinding.inflate(inflater, container, false)
        // Возвращение корневого элемента макета
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка SearchView
        val searchView: SearchView = binding.searchView
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

        // Инициализация RecyclerView
        val rv: RecyclerView = binding.recyclerView
        photoAdapter = PhotoAdapter(photoList) { photo ->
            openPhotoActivity(photo)
        }
        rv.adapter = photoAdapter
        rv.layoutManager = GridLayoutManager(context, 3) // 3 элемента в строке
    }

    // Метод для фильтрации списка фотографий
    private fun filterList(query: String) {
        val filteredList = photoList.filter { photo ->
            photo.createdAt.toString().contains(query, ignoreCase = true)
        }
        photoAdapter.filterList(filteredList.toMutableList())
    }

    // Метод для перехода на редактирование элемента
    private fun openPhotoActivity(photo: Photo) {
        val intent = Intent(context, PhotoActivity::class.java)
        intent.putExtra("photo", photo)
        startActivity(intent)
    }

    // Метод для добавления элемента
    @RequiresApi(Build.VERSION_CODES.O)
    public fun add() {
        // Создаем диалоговое окно для выбора источника фотографии
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.Widget_PhotoEditor_AlertDialog)
        builder.setTitle("Выберите источник фотографии")
        builder.setItems(arrayOf("С устройства", "С интернета")) { _, which ->
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
    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                photoAdapter.addItem(Photo("7", "7", true, selectedImageUri.toString(), LocalDate.now()))
            }
        }
    }

    // Метод получения фотографии из интернета
    @RequiresApi(Build.VERSION_CODES.O)
    private fun pickPhotoFromInternet() {
        // В этом примере мы будем использовать простой EditText для ввода URL
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.Widget_PhotoEditor_AlertDialog)
        val input = EditText(context)
        input.inputType = InputType.TYPE_TEXT_VARIATION_URI
        builder.setTitle("Введите URL фотографии")
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val url = input.text.toString()
            photoList.add(Photo("8", "8", false, url, LocalDate.now()))
        }
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    // Метод для загрузки элемента
    public fun load() {
        val selectedItems = photoAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Ни одна фотография не выбрана",
                Toast.LENGTH_LONG)
                .show()
        } else {
            for (selectedItem in selectedItems) {
                if (!selectedItem.original) {
                    // Сохранение фотографии на устройство
                    saveImageToDevice(selectedItem)
                }
                else {
                    Toast.makeText(
                        requireContext(),
                        "Фотография уже на вашем устройстве.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Метод для сохранения изображения на устройство
    private fun saveImageToDevice(selectedItem: Photo) {
        val fragment = requireContext()
        val target = object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    val file = File(fragment.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
                    try {
                        val outputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        Toast.makeText(
                            fragment,
                            "Изображение сохранено: ${file.absolutePath}",
                            Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            fragment,
                            "Ошибка при сохранении изображения",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Toast.makeText(
                    fragment,
                    "Ошибка при загрузке изображения",
                    Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                // Ничего не делаем
            }
        }

        Picasso.get().load(selectedItem.path).into(target)
    }

    // Метод для удаления элемента
    public fun delete() {
        val selectedItems = photoAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Ни одна фотография не выбрана",
                Toast.LENGTH_LONG
            ).show()
        } else {
            MaterialAlertDialogBuilder(requireContext(), R.style.Widget_PhotoEditor_AlertDialog)
                .setTitle("Подтверждение удаления")
                .setMessage("Вы действительно хотите удалить выбранные фотографии?")
                .setPositiveButton("Да") { _, _ ->
                    photoAdapter.filterList(photoAdapter.dataset.filter { it !in selectedItems }.toMutableList())
                    photoAdapter.clearSelection()
                }
                .setNegativeButton("Нет", null)
                .show()
        }
    }
}