package com.example.photoeditor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.photoeditor.databinding.FragmentUserInfoBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UserInfoFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentUserInfoBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Переменные firebase
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val firebaseLogTag: String = "Firebase_Logs"

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

        // Создание аунтификатора
        auth = Firebase.auth
        // Получение пользователя
        val cuser = Firebase.auth.currentUser

        // Чтение данных
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(firebaseLogTag, "${document.id} => ${document.data}")
                    if (cuser?.email == document.data["email"].toString()) {
                        // Отображение данных пользователя
                        binding.textViewUsername.text = document.data["username"].toString()
                        binding.textViewEmail.text = document.data["email"].toString()
                        binding.textViewCreatedAt.text = document.data["created_at"].toString()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(firebaseLogTag, getString(R.string.error_getting_documents), exception)
            }

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
        auth.signOut()
        // Создание Intent для перехода на HelloActivity
        val intent = Intent(requireContext(), HelloActivity::class.java)
        // Переход на HelloActivity
        startActivity(intent)
    }
}