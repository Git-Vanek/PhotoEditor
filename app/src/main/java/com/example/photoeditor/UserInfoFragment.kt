package com.example.photoeditor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.photoeditor.databinding.FragmentUserInfoBinding
import java.time.LocalDate

class UserInfoFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentUserInfoBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация View Binding для макета фрагмента
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        // Возвращение корневого элемента макета
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Создание данных пользователя
        val user = User("123", "Vanya Rocker", "vanyaOK@gmail.com", "123", LocalDate.parse("2017-12-12"))

        // Отображение данных пользователя
        binding.textViewUsername.text = user.username
        binding.textViewEmail.text = user.email
        binding.textViewCreatedAt.text = user.createdAt.toString()

        // Установка обработчика нажатия для кнопки "Назад"
        binding.buttonBack.setOnClickListener {
            back()
        }

        // Установка обработчика нажатия для кнопки выхода
        binding.buttonLogout.setOnClickListener {
            logout()
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

    // Функция для обработки выхода
    private fun logout() {
        // Создание Intent для перехода на HelloActivity
        val intent = Intent(requireContext(), HelloActivity::class.java)
        // Переход на HelloActivity
        startActivity(intent)
    }
}