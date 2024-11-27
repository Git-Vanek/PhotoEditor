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
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Выгрузка и загрузка параметров
        loadSettings()

        // Установка обработчика нажатия для кнопки "Назад"
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Установка обработчика нажатия для кнопки "Сохранить"
        binding.buttonSave.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        // Загрузка сохраненного формата изображения и установка его в Spinner
        val savedImageFormat = sharedPreferences.getString("image_format", null)
        if (savedImageFormat != null) {
            val imageFormatOptions = resources.getStringArray(R.array.image_format)
            val position = imageFormatOptions.indexOf(savedImageFormat)
            binding.spinnerImageFormat.setSelection(position)
        }

        // Загрузка сохраненной темы и установка её в Spinner
        val savedTheme = sharedPreferences.getString("theme", null)
        if (savedTheme != null) {
            val themeOptions = resources.getStringArray(R.array.themes)
            val position = themeOptions.indexOf(savedTheme)
            binding.spinnerTheme.setSelection(position)
        }

        // Загрузка сохраненного количества элементов в строке и установка его в Spinner
        val savedColumns = sharedPreferences.getInt("columns", 2)
        binding.spinnerColumns.setSelection(savedColumns - 1)

        // Загрузка сохраненного показа дат и установка его в Spinner
        val savedShowDates = sharedPreferences.getBoolean("show_dates", true)
        binding.spinnerShowDates.setSelection(if (savedShowDates) 0 else 1)
    }

    private fun saveSettings() {
        // Получение выбранных значений из Spinner
        val selectedImageFormat = binding.spinnerImageFormat.selectedItem.toString()
        val selectedTheme = binding.spinnerTheme.selectedItem.toString()
        val selectedColumns = binding.spinnerColumns.selectedItem.toString().toInt()
        val selectedShowDates = binding.spinnerShowDates.selectedItem.toString() == getString(R.string.yes)

        // Сохранение значений в SharedPreferences
        with(sharedPreferences.edit()) {
            putString("image_format", selectedImageFormat)
            putString("theme", selectedTheme)
            putInt("columns", selectedColumns)
            putBoolean("show_dates", selectedShowDates)
            apply()
        }

        // Подтверждение сохранения
        Toast.makeText(
            context,
            getString(R.string.settings_saved),
            Toast.LENGTH_SHORT
        ).show()
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
}