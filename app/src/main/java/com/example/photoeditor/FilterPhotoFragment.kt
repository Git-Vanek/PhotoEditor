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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoeditor.databinding.FragmentFilterPhotoBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.SaveSettings
import java.io.File
import java.io.IOException

@Suppress("DEPRECATION")
class FilterPhotoFragment : Fragment(), FilterAdapter.OnFilterSelectedListener {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentFilterPhotoBinding
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

    // Переменные для работы с фильтрами
    private lateinit var filterAdapter: FilterAdapter
    private val mPairList = listOf(
        Pair(R.drawable.original, PhotoFilter.NONE),
        Pair(R.drawable.auto_fix, PhotoFilter.AUTO_FIX),
        Pair(R.drawable.brightness, PhotoFilter.BRIGHTNESS),
        Pair(R.drawable.contrast, PhotoFilter.CONTRAST),
        Pair(R.drawable.documentary, PhotoFilter.DOCUMENTARY),
        Pair(R.drawable.dual_tone, PhotoFilter.DUE_TONE),
        Pair(R.drawable.fill_light, PhotoFilter.FILL_LIGHT),
        Pair(R.drawable.fish_eye, PhotoFilter.FISH_EYE),
        Pair(R.drawable.grain, PhotoFilter.GRAIN),
        Pair(R.drawable.gray_scale, PhotoFilter.GRAY_SCALE),
        Pair(R.drawable.lomish, PhotoFilter.LOMISH),
        Pair(R.drawable.negative, PhotoFilter.NEGATIVE),
        Pair(R.drawable.posterize, PhotoFilter.POSTERIZE),
        Pair(R.drawable.saturate, PhotoFilter.SATURATE),
        Pair(R.drawable.sepia, PhotoFilter.SEPIA),
        Pair(R.drawable.sharpen, PhotoFilter.SHARPEN),
        Pair(R.drawable.temprature, PhotoFilter.TEMPERATURE),
        Pair(R.drawable.tint, PhotoFilter.TINT),
        Pair(R.drawable.vignette, PhotoFilter.VIGNETTE),
        Pair(R.drawable.cross_process, PhotoFilter.CROSS_PROCESS),
        Pair(R.drawable.b_n_w, PhotoFilter.BLACK_WHITE),
        Pair(R.drawable.flip_horizental, PhotoFilter.FLIP_HORIZONTAL),
        Pair(R.drawable.flip_vertical, PhotoFilter.FLIP_VERTICAL),
        Pair(R.drawable.rotate, PhotoFilter.ROTATE)
    )

    // Геттер для переменной binding
    private val binding get() = _binding

    companion object {
        private const val ARG_PHOTO = "photo"

        fun newInstance(photo: Photo): FilterPhotoFragment {
            val fragment = FilterPhotoFragment()
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
        _binding = FragmentFilterPhotoBinding.inflate(inflater, container, false)
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

        val rv = binding.recyclerView
        filterAdapter = FilterAdapter(mPairList, this)
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = filterAdapter

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
    }

    override fun onFilterSelected(filter: PhotoFilter) {
        photoEditor.setFilterEffect(filter)
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
}