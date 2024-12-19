package com.example.photoeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.databinding.FragmentPagePhotoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class PagePhotoFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentPagePhotoBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Список фотографий
    private lateinit var photoList: MutableList<Photo>

    // Адаптеры для RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var photoWithDatesAdapter: PhotoWithDatesAdapter

    // Ключ для SharedPreferences
    private val settings: String = "my_settings"
    private lateinit var sharedPreferences: SharedPreferences

    // Переменные параметров
    private var imageFormat: String = "jpg"
    private var gridCount: Int = 2
    private var showDates: Boolean = false

    // Переменная хранения пути для сделанной фотографии
    private var currentPhotoPath: String? = null

    // Переменные firebase
    private val db = Firebase.firestore
    private var user: FirebaseUser? = null
    private val firebaseLogTag: String = "Firebase_Logs"

    // Текущая дата
    @RequiresApi(Build.VERSION_CODES.O)
    private val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    companion object {
        private const val ARG_PHOTO_LIST = "photo_list"

        fun newInstance(list: MutableList<Photo>): PagePhotoFragment {
            val fragment = PagePhotoFragment()
            val args = Bundle()
            args.putSerializable(ARG_PHOTO_LIST, ArrayList(list))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получение списка
        arguments?.let {
            @Suppress("UNCHECKED_CAST")
            photoList = it.getSerializable(ARG_PHOTO_LIST) as MutableList<Photo>
        }
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

        // Получение пользователя
        user = Firebase.auth.currentUser

        // Инициализация SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(settings, Context.MODE_PRIVATE)
        // Получение параметров
        imageFormat = sharedPreferences.getString("image_format", "jpg").toString()
        gridCount = sharedPreferences.getInt("columns", 2)
        showDates = sharedPreferences.getBoolean("show_dates", false)

        // Инициализация RecyclerView
        val rv: RecyclerView = binding.recyclerView
        photoAdapter = PhotoAdapter(photoList, context) { photo ->
            openPhotoActivity(photo)
        }
        if (showDates) {
            // Установка RecyclerView с датами
            photoWithDatesAdapter = PhotoWithDatesAdapter(photoAdapter, gridCount, context)
            rv.adapter = photoWithDatesAdapter
            rv.layoutManager = LinearLayoutManager(context)
        }
        else {
            // Установка RecyclerView без дат
            rv.adapter = photoAdapter
            rv.layoutManager = GridLayoutManager(context, gridCount)
        }
    }

    // Метод для фильтрации списка фотографий
    fun filterList(query: String) {
        if (!showDates) {
            val filteredList = photoList.filter { photo ->
                photo.createdAt.contains(query, ignoreCase = true)
            }
            photoAdapter.filterList(filteredList.toMutableList())
        }
        else {
            Toast.makeText(
                requireContext(),
                getString(R.string.func_not_available),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Метод для перехода на редактирование элемента
    private fun openPhotoActivity(photo: Photo) {
        val intent = Intent(context, PhotoActivity::class.java)
        intent.putExtra("photo", photo)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun add() {
        if (!showDates) {
            // Создаем диалоговое окно для выбора источника фотографии
            val builder = MaterialAlertDialogBuilder(requireContext(), R.style.Widget_PhotoEditor_AlertDialog)
            builder.setTitle(getString(R.string.choose_photo_source))
            builder.setItems(arrayOf(getString(R.string.from_device), getString(R.string.from_internet), getString(R.string.take_photo))) { _, which ->
                when (which) {
                    0 -> pickPhotoFromDevice()
                    1 -> pickPhotoFromInternet()
                    2 -> takePhoto()
                }
            }
            builder.show()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.func_not_available),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Метод получения фотографии с устройства
    @RequiresApi(Build.VERSION_CODES.O)
    private fun pickPhotoFromDevice() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoLauncher.launch(intent)
    }

    // Обработка результата выбора фотографии с устройства
    @RequiresApi(Build.VERSION_CODES.O)
    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                addPhoto(Photo("", date, true, selectedImageUri.toString(), user != null))
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
        builder.setTitle(getString(R.string.enter_photo_url))
        builder.setView(input)
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            val url = input.text.toString()
            addPhoto(Photo("", date, false, url, user != null))
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }


    // Метод для создания фотографии с использованием камеры
    @RequiresApi(Build.VERSION_CODES.O)
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: Exception) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(requireContext(), "com.example.photoeditor.fileprovider", it)
                currentPhotoPath = it.absolutePath
                takePicture.launch(photoURI)
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_camera_app_found),
                Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(Exception::class)
    private fun createImageFile(): File {
        // Получаем директорию Pictures
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Создаем поддиректорию Photos внутри Pictures
        val photosDir = File(storageDir, "Photos")

        // Убедитесь, что директория существует или создайте её
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }

        // Создаем временный файл в директории Photos
        return File.createTempFile(
            "img_${System.currentTimeMillis()}_", /* prefix */
            ".$imageFormat", /* suffix */
            photosDir /* directory */
        ).apply {
            // Сохраняем путь к файлу для последующего использования
            currentPhotoPath = absolutePath
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            // Загрузка изображения с устройства
            val photoUri = FileProvider.getUriForFile(requireContext(), "com.example.photoeditor.fileprovider", File(
                currentPhotoPath.toString()
            ))
            addPhoto(Photo("", date, true, photoUri.toString(), user != null))
        }
    }

    // Метод для загрузки элемента
    fun load() {
        if (!showDates) {
            val selectedItems = photoAdapter.getSelectedItems()
            if (selectedItems.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.no_photos_selected,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                for (selectedItem in selectedItems) {
                    if (!selectedItem.original) {
                        saveImageToDevice(selectedItem)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.the_photo_is_already_on_your_device),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        else {
            Toast.makeText(
                requireContext(),
                getString(R.string.func_not_available),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Метод для сохранения изображения на устройство
    private fun saveImageToDevice(selectedItem: Photo) {
        val fragment = context
        val target = object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    val fileName = "img_${System.currentTimeMillis()}." + imageFormat

                    // Получаем директорию Pictures
                    val storageDir: File? = fragment?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                    // Создаем поддиректорию Photos внутри Pictures
                    val photosDir = File(storageDir, "Photos")

                    // Убедитесь, что директория существует или создайте её
                    if (!photosDir.exists()) {
                        photosDir.mkdirs()
                    }

                    // Создаем файл в директории Photos
                    val file = File(photosDir, fileName)

                    try {
                        val outputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        Toast.makeText(
                            fragment,
                            requireContext().getString(R.string.image_saved) + ": " + file.absolutePath,
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            fragment,
                            requireContext().getString(R.string.error_saving_image),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Toast.makeText(
                    fragment,
                    requireContext().getString(R.string.error_loading_image),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                // Ничего не делаем
            }
        }

        Picasso.get().load(selectedItem.path).into(target)
    }

    // Метод для удаления элемента
    @RequiresApi(Build.VERSION_CODES.O)
    fun delete() {
        if (!showDates) {
            val selectedItems = photoAdapter.getSelectedItems()
            if (selectedItems.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_photos_selected),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                MaterialAlertDialogBuilder(requireContext(), R.style.Widget_PhotoEditor_AlertDialog)
                    .setTitle(getString(R.string.confirm_deletion))
                    .setMessage(getString(R.string.want_to_delete))
                    .setPositiveButton(R.string.yes) { _, _ ->
                        removePhotos(selectedItems)
                    }
                    .setNegativeButton(getString(R.string.no), null)
                    .show()
            }
        }
        else {
            Toast.makeText(
                requireContext(),
                getString(R.string.func_not_available),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Метод добавления фотографии
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addPhoto(photo: Photo) {
        val photoRef = db.collection("Photos").document()
        val photoId = photoRef.id
        val photoData = hashMapOf(
            "id" to photoId,
            "created_at" to photo.createdAt,
            "original" to photo.original,
            "path" to photo.path,
            "private" to photo.private
        )

        photoRef.set(photoData)
            .addOnSuccessListener {
                if (photo.private) {
                    val userRef = db.collection("Users").document(user!!.uid)
                    userRef.update("photoRefs", FieldValue.arrayUnion(photoId))
                        .addOnSuccessListener {
                            Log.d(firebaseLogTag, "Photo reference added to user document")
                            getDataMain()
                        }
                        .addOnFailureListener { e ->
                            Log.w(firebaseLogTag, "Error updating user document", e)
                            getDataMain()
                        }
                } else {
                    Log.d(firebaseLogTag, "Photo is public, no reference added to user document")
                    getDataMain()
                }
            }
            .addOnFailureListener { e ->
                Log.w(firebaseLogTag, "Error adding photo document", e)
                getDataMain()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataMain() {
        MainActivity.mainFragment.getData()
    }

    // Метод удаления выбранных фотографий
    @RequiresApi(Build.VERSION_CODES.O)
    private fun removePhotos(photos: MutableList<Photo>) {
        for (photo in photos) {
            val photoId = photo.id
            val photoRef = db.collection("Photos").document(photoId)
            photoRef.delete()
                .addOnSuccessListener {
                    val userRef = db.collection("Users").document(user!!.uid)
                    userRef.update("photoRefs", FieldValue.arrayRemove(photoId))
                        .addOnSuccessListener {
                            Log.d(firebaseLogTag, "Photo reference removed from user document")
                        }
                        .addOnFailureListener { e ->
                            Log.w(firebaseLogTag, "Error updating user document", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(firebaseLogTag, "Error deleting photo document", e)
                }
        }
        getDataMain()
    }
}