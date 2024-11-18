package com.example.photoeditor

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photoeditor.databinding.FragmentSighinBinding

class SighinFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentSighinBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация View Binding для макета фрагмента
        _binding = FragmentSighinBinding.inflate(inflater, container, false)
        // Возвращение корневого элемента макета
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Установка обработчика нажатия для кнопки авторизации
        binding.buttonSignIn.setOnClickListener {
            signIn()
        }

        // Установка обработчика нажатия для кнопки регистрации
        binding.buttonSignUp.setOnClickListener {
            signUp()
        }

        // Установка обработчика нажатия для кнопки входа гостя
        binding.buttonGuest.setOnClickListener {
            guest()
        }
    }

    // Функция для обработки авторизации
    private fun signIn() {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Запуск MainActivity
        startActivity(intent)
    }

    // Функция для обработки регистрации
    private fun signUp() {
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на SignupFragment
            .replace(R.id.card, SignupFragment())
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }

    // Функция для обработки входа гостя
    private fun guest() {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Запуск MainActivity
        startActivity(intent)
    }
}