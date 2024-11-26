package com.example.photoeditor

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.photoeditor.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentSettingsBinding
    // Геттер для переменной binding
    private val binding get() = _binding
    // Ключ для SharedPreferences
    private val settings: String = "my_settings"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация View Binding для макета фрагмента
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        // Возвращение корневого элемента макета
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(settings, Context.MODE_PRIVATE)

        // Установка адаптеров для Spinner
        val imageFormatOptions = arrayOf("PNG", "JPG")
        val imageFormatAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, imageFormatOptions)
        imageFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerImageFormat.adapter = imageFormatAdapter

        // Загрузка сохраненного формата изображения и установка его в Spinner
        loadSavedImageFormat()

        // Установка обработчика нажатия для кнопки "Назад"
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Установка обработчика нажатия для кнопки "Сохранить"
        binding.buttonSave.setOnClickListener {
            save()
        }
    }


    // Функция для загрузки сохраненного формата изображения
    private fun loadSavedImageFormat() {
        val savedImageFormat = sharedPreferences.getString("image_format", null)
        if (savedImageFormat != null) {
            @Suppress("UNCHECKED_CAST") val position = (binding.spinnerImageFormat.adapter as ArrayAdapter<String>).getPosition(savedImageFormat)
            binding.spinnerImageFormat.setSelection(position)
        }
    }

    // Функция для обработки возврата
    private fun back() {
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на MainFragment
            .replace(R.id.mainContent, MainFragment())
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }

    private fun save() {
        // Получение выбранных значений из Spinner
        val selectedImageFormat = binding.spinnerImageFormat.selectedItem.toString()

        // Сохранение значений в SharedPreferences
        with(sharedPreferences.edit()) {
            putString("image_format", selectedImageFormat)
            apply()
        }

        // Подтверждение сохранения
        Toast.makeText(
            context,
            getString(R.string.settings_saved),
            Toast.LENGTH_SHORT)
            .show()
    }
}