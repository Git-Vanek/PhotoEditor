package com.example.photoeditor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.photoeditor.databinding.FragmentSignupBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SignupFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentSignupBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Переменные firebase
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val firebaseLogTag: String = "Firebase_Logs"

    // Переменные авторизации
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    // Интерфейс для взаимодействия с активностью
    interface OnSignUpListener {
        fun onSignUp(email: String, password: String)
    }

    companion object {
        private const val ARG_EMAIL = "name"
        private const val ARG_PASSWORD = "password"

        fun newInstance(email: String, password: String): SignupFragment {
            val fragment = SignupFragment()
            val args = Bundle()
            args.putSerializable(ARG_EMAIL, email)
            args.putSerializable(ARG_PASSWORD, password)
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
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        // Возвращение корневого элемента макета
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("DEPRECATION")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Создание аутентификатора
        auth = Firebase.auth

        // Получение аргументов
        email = arguments?.getSerializable(ARG_EMAIL).toString()
        password = arguments?.getSerializable(ARG_PASSWORD).toString()

        // Установка значений
        setData(email, password)

        // Установка обработчика нажатия для кнопки регистрации
        binding.buttonSignUp.setOnClickListener {
            signUp()
        }

        // Установка обработчика нажатия для кнопки возврата
        binding.buttonBack.setOnClickListener {
            back()
        }
    }

    // Функция обновления значений
    private fun getData() {
        username = binding.editUsername.text.toString()
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()
    }

    // Функция установки значений
    private fun setData(email: String, password: String) {
        binding.editEmail.setText(email)
        binding.editPassword.setText(password)
    }

    // Функция обновления данных
    fun updateData(email: String, password: String) {
        // Установка значений
        setData(email, password)
    }

    // Функция для обработки регистрации
    @RequiresApi(Build.VERSION_CODES.O)
    private fun signUp() {
        // Обновление значений
        getData()

        // Проверка заполнения
        if (username != "" && email != "" && password != "") {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(firebaseLogTag, "createUserWithEmail:success")
                        val user = auth.currentUser
                        if (user != null) {
                            val userData = hashMapOf(
                                "created_at" to LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                "email" to email,
                                "password" to password,
                                "photoRefs" to listOf<String>(),
                                "username" to username
                            )
                            db.collection("Users").document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Log.d(firebaseLogTag, "User document added with ID: ${user.uid}")
                                    goMain()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(firebaseLogTag, "Error adding user document", e)
                                }
                        }
                    } else {
                        Log.w(firebaseLogTag, "createUserWithEmail:failure", task.exception)
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> getString(R.string.error_weak_password)
                            is FirebaseAuthInvalidCredentialsException -> getString(R.string.error_invalid_email)
                            is FirebaseAuthUserCollisionException -> getString(R.string.error_email_already_in_use)
                            is FirebaseNetworkException -> getString(R.string.error_network_request_failed)
                            else -> getString(R.string.error_unknown)
                        }
                        Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }
        else {
            Toast.makeText(
                requireContext(),
                getString(R.string.error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Функция для обработки возврата
    private fun back() {
        // Обновление значений
        getData()

        // Передача данных в активность
        (activity as? OnSignUpListener)?.onSignUp(email, password)
    }

    // Переход на главную активность
    private fun goMain() {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Запуск MainActivity
        startActivity(intent)
    }
}