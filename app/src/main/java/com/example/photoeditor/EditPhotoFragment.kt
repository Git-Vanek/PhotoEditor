package com.example.photoeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.photoeditor.databinding.FragmentEditPhotoBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveSettings
import java.io.File
import java.io.IOException
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class EditPhotoFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentEditPhotoBinding
    // Инициализация переменных для PhotoEditor
    private lateinit var photoEditorView: PhotoEditorView
    private lateinit var photoEditor: PhotoEditor

    // Переменная фотографии
    private lateinit var photo: Photo

    // Ключ для SharedPreferences
    private val settings: String = "my_settings"
    private lateinit var sharedPreferences: SharedPreferences

    // Переменные параметров
    private var imageFormat: String = "jpg"

    // Геттер для переменной binding
    private val binding get() = _binding

    // Интерфейс для взаимодействия с активностью
    interface OnEditPhotoListener {
        fun onEditPhoto(photo: Photo)
    }

    companion object {
        private const val ARG_PHOTO = "photo"

        fun newInstance(photo: Photo): EditPhotoFragment {
            val fragment = EditPhotoFragment()
            val args = Bundle()
            args.putSerializable(ARG_PHOTO, photo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация View Binding для макета фрагмента
        _binding = FragmentEditPhotoBinding.inflate(inflater, container, false)
        // Возвращение корневого элемента макета
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Получение аргументов
        photo = arguments?.getSerializable(ARG_PHOTO) as Photo

        // Инициализация SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(settings, Context.MODE_PRIVATE)
        // Получение параметров
        imageFormat = sharedPreferences.getString("image_format", "jpg").toString()

        photoEditorView = binding.photoEditorView
        photoEditor = PhotoEditor.Builder(requireContext(), photoEditorView)
            .setPinchTextScalable(true)
            .build()
        setPhoto()

        // Установка обработчика нажатия для кнопки возврата
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Установка обработчика нажатия для кнопки сохранения
        binding.buttonSave.setOnClickListener {
            save()
        }

        // Установка обработчика нажатия для кнопки обрезания
        binding.buttonCrop.setOnClickListener {
            crop()
        }
    }

    // Метод масштабирования изображения
    private fun scaleBitmapToFitView(bitmap: Bitmap, view: PhotoEditorView) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val viewWidth = view.width
                val viewHeight = view.height

                if (viewWidth != 0 && viewHeight != 0) {
                    val bitmapWidth = bitmap.width
                    val bitmapHeight = bitmap.height

                    val scaleWidth = viewWidth.toFloat() / bitmapWidth
                    val scaleHeight = viewHeight.toFloat() / bitmapHeight
                    val scale = scaleWidth.coerceAtMost(scaleHeight)

                    val matrix = Matrix()
                    matrix.postScale(scale, scale)

                    // Центрирование изображения
                    val scaledBitmapWidth = (bitmapWidth * scale).toInt()
                    val scaledBitmapHeight = (bitmapHeight * scale).toInt()
                    val translateX = (viewWidth - scaledBitmapWidth) / 2
                    val translateY = (viewHeight - scaledBitmapHeight) / 2
                    matrix.postTranslate(translateX.toFloat(), translateY.toFloat())

                    val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true)
                    photoEditorView.source.setImageBitmap(scaledBitmap)
                }
            }
        })
    }

    // Функция обновления данных
    fun updateData(photo: Photo) {
        this@EditPhotoFragment.photo = photo
        setPhoto()
    }

    // Метод отображения изображения
    private fun setPhoto() {
        // Отображение изображения
        if (photo.original) {
            // Загрузка изображения с устройства
            Picasso.get()
                .load(Uri.parse(photo.path))
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        bitmap?.let {
                            scaleBitmapToFitView(it, photoEditorView)
                        }
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        // Обработка ошибки загрузки изображения
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        // Обработка подготовки загрузки изображения
                    }
                })
        } else {
            // Загрузка изображения из интернета
            Picasso.get()
                .load(photo.path)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        bitmap?.let {
                            scaleBitmapToFitView(it, photoEditorView)
                        }
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        // Обработка ошибки загрузки изображения
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        // Обработка подготовки загрузки изображения
                    }
                })
        }
    }

    // Метод возвращения на просмотр фотографии
    private fun back() {
        // Передача данных в активность
        (activity as? OnEditPhotoListener)?.onEditPhoto(photo)
    }

    // Метод сохранения
    private fun save() {
        val saveSettings = SaveSettings.Builder()
            .setClearViewsEnabled(true)
            .setTransparencyEnabled(true)
            .build()

        photoEditor.saveAsFile(
            createImageFile().absolutePath,
            saveSettings,
            object : PhotoEditor.OnSaveListener {
                override fun onSuccess(imagePath: String) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.image_saved),
                        Toast.LENGTH_SHORT)
                        .show()
                    back()
                }

                override fun onFailure(exception: Exception) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_saving_image),
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }

    // Метод для создания файла изображения
    @Throws(IOException::class)
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
        )
    }

    private fun crop() {
        val bitmap = photoEditorView.source.drawable.toBitmap()
        val uri = getImageUri(bitmap)
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))
        val options = UCrop.Options()
        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primary))
        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary))
        options.setToolbarWidgetColor(ContextCompat.getColor(requireContext(), R.color.onPrimary))
        UCrop.of(uri, destinationUri)
            .withOptions(options)
            .withAspectRatio(1f, 1f)
            .start(requireContext(), this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, resultUri!!)
            photoEditorView.source.setImageBitmap(bitmap)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Toast.makeText(requireContext(), cropError?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val tempFile = File(requireContext().cacheDir, "temp_image.jpg")
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val byteArray = bytes.toByteArray()

        val fos = FileOutputStream(tempFile)
        fos.write(byteArray)
        fos.flush()
        fos.close()

        return Uri.fromFile(tempFile)
    }
}