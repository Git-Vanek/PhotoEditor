package com.example.photoeditor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photoeditor.databinding.FragmentViewPhotoBinding
import com.squareup.picasso.Picasso

class ViewPhotoFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentViewPhotoBinding
    // Геттер для переменной binding
    private val binding get() = _binding

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
        val photo = arguments?.getSerializable(ARG_PHOTO) as? Photo

        // Отображение изображения
        if (photo != null) {
            if (photo.original) {
                binding.photoView.setImageResource(photo.path.toInt())
            } else {
                Picasso.get()
                    .load(photo.path)
                    .error(R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.photoView)
            }
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

    // Функция для обработки рисования
    private fun draw() {
        TODO("Not yet implemented")
    }

    // Функция для обработки фильтрации
    private fun filter() {
        TODO("Not yet implemented")
    }

    // Функция для обработки редактирования
    private fun edit() {
        TODO("Not yet implemented")
    }
}