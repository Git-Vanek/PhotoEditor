package com.example.photoeditor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photoeditor.databinding.FragmentViewPhotoBinding
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class ViewPhotoFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentViewPhotoBinding
    // Геттер для переменной binding
    private val binding get() = _binding
    private lateinit var photo: Photo

    companion object {
        private const val ARG_PHOTO = "photo"

        fun newInstance(photo: Photo): ViewPhotoFragment {
            val fragment = ViewPhotoFragment()
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
        _binding = FragmentViewPhotoBinding.inflate(inflater, container, false)
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

        // Отображение изображения
        if (photo.original) {
            // Загрузка изображения с устройства
            Picasso.get()
                .load(Uri.parse(photo.path))
                .into(binding.photoView)
        } else {
            // Загрузка изображения из интернета
            Picasso.get()
                .load(photo.path)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.photoView)
        }

        // Установка обработчика нажатия для кнопки возврата
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Установка обработчика нажатия для кнопки рисования
        binding.buttonDraw.setOnClickListener {
            draw()
        }

        // Установка обработчика нажатия для кнопки фильтрации
        binding.buttonFilter.setOnClickListener {
            filter()
        }

        // Установка обработчика нажатия для кнопки редактирования
        binding.buttonEdit.setOnClickListener {
            edit()
        }
    }

    private fun back() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }

    // Функция для обработки рисования
    private fun draw() {
        // Создание экземпляра фрагмента DrawPhotoFragment с передачей переменной photo
        val drawPhotoFragment = DrawPhotoFragment.newInstance(photo)
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на DrawPhotoFragment
            .replace(R.id.imageContent, drawPhotoFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }

    // Функция для обработки фильтрации
    private fun filter() {
        // Создание экземпляра фрагмента FilterPhotoFragment с передачей переменной photo
        val filterPhotoFragment = FilterPhotoFragment.newInstance(photo)
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на FilterPhotoFragment
            .replace(R.id.imageContent, filterPhotoFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }

    // Функция для обработки редактирования
    private fun edit() {
        // Создание экземпляра фрагмента EditPhotoFragment с передачей переменной photo
        val editPhotoFragment = EditPhotoFragment.newInstance(photo)
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на EditPhotoFragment
            .replace(R.id.imageContent, editPhotoFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }
}