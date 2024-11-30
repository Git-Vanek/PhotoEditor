package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.photoeditor.databinding.FragmentDrawPhotoBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveFileResult
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class DrawPhotoFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentDrawPhotoBinding
    // Инициализация переменных для PhotoEditor
    private lateinit var photoEditorView: PhotoEditorView
    private lateinit var photoEditor: PhotoEditor
    // Переменная фотографии
    private lateinit var photo: Photo

    // Геттер для переменной binding
    private val binding get() = _binding

    companion object {
        private const val ARG_PHOTO = "photo"

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
                            photoEditorView.source.setImageBitmap(it)
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
                            photoEditorView.source.setImageBitmap(it)
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
        lifecycleScope.launch {
            val result = photoEditor.saveAsFile(filePath)
            if (result is SaveFileResult.Success) {
                showSnackbar("Image saved!")
            } else {
                showSnackbar("Couldn't save image")
            }
        }
    }

    // Метод переключения на кисти
    private fun brush() {
        // Включите режим рисования
        photoEditor.setBrushDrawingMode(true)
        photoEditor.brushColor = Color.RED
        photoEditor.brushSize = 10f
    }

    // Метод переключения цвета
    private fun color() {

    }

    // Метод изменения размера
    private fun size() {

    }

    // Метод переключения на ластик
    private fun eraser() {

    }
}