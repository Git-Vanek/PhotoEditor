package com.example.photoeditor

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.photoeditor.databinding.FragmentEditPhotoBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveSettings
import java.io.File
import java.io.IOException

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
                            bitmap?.let {
                                scaleBitmapToFitView(it, photoEditorView)
                            }
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
                            bitmap?.let {
                                scaleBitmapToFitView(it, photoEditorView)
                            }
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

        // Установка обработчика нажатия для кнопки возврата
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Установка обработчика нажатия для кнопки отката изменения
        binding.buttonEditBack.setOnClickListener {
            backBrush()
        }

        // Установка обработчика нажатия для кнопки отката отката изменения
        binding.buttonEditForvard.setOnClickListener {
            editForvard()
        }

        // Установка обработчика нажатия для кнопки сохранения
        binding.buttonSave.setOnClickListener {
            save()
        }

        // Установка обработчика нажатия для кнопки сохранения
        binding.buttonCrop.setOnClickListener {
            crop()
        }
    }

    // Метод возвращения на просмотр фотографии
    private fun back() {
        // Создание экземпляра фрагмента ViewPhotoFragment с передачей переменной photo
        val viewPhotoFragment = ViewPhotoFragment.newInstance(photo)
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на ViewPhotoFragment
            .replace(R.id.imageContent, viewPhotoFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
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
                    val scale = Math.min(scaleWidth, scaleHeight)

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

    // Метод отката изменения
    private fun backBrush() {
        photoEditor.undo();
    }

    // Метод отката отката изменения
    private fun editForvard() {
        photoEditor.redo();
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
                    Toast.makeText(requireContext(), getString(R.string.image_saved), Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(exception: Exception) {
                    Toast.makeText(requireContext(), getString(R.string.error_saving_image), Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Метод для создания файла изображения
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "img_${System.currentTimeMillis()}_", /* prefix */
            "." + imageFormat, /* suffix */
            storageDir /* directory */
        )
    }

    // Метод обрезания
    private fun crop() {

    }
}