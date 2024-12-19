package com.example.photoeditor

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
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
import androidx.core.content.ContextCompat
import com.example.photoeditor.databinding.FragmentDrawPhotoBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveSettings
import android.widget.SeekBar
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException

@Suppress("DEPRECATION")
class DrawPhotoFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentDrawPhotoBinding
    // Инициализация переменных для PhotoEditor
    private lateinit var photoEditorView: PhotoEditorView
    private lateinit var photoEditor: PhotoEditor

    // Переменная фотографии
    private lateinit var photo: Photo

    // Флаги для кнопок
    private var flagBrush: Boolean = false
    private var flagEraser: Boolean = false

    // Ключ для SharedPreferences
    private val settings: String = "my_settings"
    private lateinit var sharedPreferences: SharedPreferences

    // Переменные параметров
    private var imageFormat: String = "jpg"

    // Геттер для переменной binding
    private val binding get() = _binding

    companion object {
        private const val ARG_PHOTO = "photo"

        // Метод для создания нового экземпляра фрагмента
        fun newInstance(photo: Photo): DrawPhotoFragment {
            val fragment = DrawPhotoFragment()
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
        _binding = FragmentDrawPhotoBinding.inflate(inflater, container, false)
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

        // Установка обработчика нажатия для кнопки кисти
        binding.buttonBrush.setOnClickListener {
            brush()
        }

        // Установка обработчика нажатия для кнопки цвета
        binding.buttonColor.setOnClickListener {
            color()
        }

        // Установка обработчика нажатия для кнопки размера
        binding.buttonSize.setOnClickListener {
            size()
        }

        // Установка обработчика нажатия для кнопки ластика
        binding.buttonEraser.setOnClickListener {
            eraser()
        }

        // Установка цвета и размера кисти по умолчанию
        photoEditor.brushColor = Color.RED
        photoEditor.brushSize = 10f
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

    // Метод отката изменения
    private fun backBrush() {
        photoEditor.undo()
    }

    // Метод отката отката изменения
    private fun editForvard() {
        photoEditor.redo()
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

    // Метод переключения на кисти
    private fun brush() {
        if (flagEraser) {
            eraser() // Выключить режим ластика, если он включен
        }
        flagBrush = !flagBrush
        photoEditor.setBrushDrawingMode(flagBrush)
        updateBrushButtonBackground()
    }

    // Метод переключения цвета
    private fun color() {
        val colors = resources.getStringArray(R.array.colors)
        val colorCodes = intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK, Color.WHITE)

        MaterialAlertDialogBuilder(requireContext(), R.style.Widget_PhotoEditor_AlertDialog)
            .setTitle(getString(R.string.choose_color))
            .setItems(colors) { _, which ->
                photoEditor.brushColor = colorCodes[which]
            }
            .show()
    }

    // Метод изменения размера
    private fun size() {
        val seekBar = SeekBar(requireContext())
        seekBar.max = 200
        seekBar.progress = (photoEditor.brushSize * 10).toInt()

        MaterialAlertDialogBuilder(requireContext(), R.style.Widget_PhotoEditor_AlertDialog)
            .setTitle(getString(R.string.choose_brush_size))
            .setView(seekBar)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                photoEditor.brushSize = (seekBar.progress / 10f)
            }
            .show()
    }

    // Метод переключения на ластик
    private fun eraser() {
        if (flagBrush) {
            brush() // Выключить режим кисти, если он включен
        }
        flagEraser = !flagEraser
        photoEditor.brushEraser()
        updateEraserButtonBackground()
    }

    // Метод обновления фона кнопки кисти
    private fun updateBrushButtonBackground() {
        if (flagBrush) {
            binding.buttonBrush.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.onBackground))
        } else {
            binding.buttonBrush.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background))
        }
    }

    // Метод обновления фона кнопки ластика
    private fun updateEraserButtonBackground() {
        if (flagEraser) {
            binding.buttonEraser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.onBackground))
        } else {
            binding.buttonEraser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background))
        }
    }
}