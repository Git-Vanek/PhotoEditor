package com.example.photoeditor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.photoeditor.databinding.FragmentSighinBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth

class SigningFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentSighinBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Переменные firebase
    private lateinit var auth: FirebaseAuth
    private val firebaseLogTag: String = "Firebase_Logs"

    // Переменные авторизации
    private lateinit var email: String
    private lateinit var password: String

    // Интерфейс для взаимодействия с активностью
    interface OnSignInListener {
        fun onSignIn(email: String, password: String)
    }

    companion object {
        private const val ARG_EMAIL = "name"
        private const val ARG_PASSWORD = "password"

        fun newInstance(email: String, password: String): SigningFragment {
            val fragment = SigningFragment()
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
        _binding = FragmentSighinBinding.inflate(inflater, container, false)
        // Возвращение корневого элемента макета
        return binding.root
    }

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

    // Функция обновления значений
    private fun getData() {
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

    // Функция для обработки авторизации
    private fun signIn() {
        // Обновление значений
        getData()

        // Проверка заполнения
        if (email != "" && password != "") {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(firebaseLogTag, "signInWithEmail:success")
                        goMain()
                    } else {
                        Log.w(firebaseLogTag, "signInWithEmail:failure", task.exception)
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> getString(R.string.error_weak_password)
                            is FirebaseAuthInvalidCredentialsException -> getString(R.string.error_credential)
                            is FirebaseAuthUserCollisionException -> getString(R.string.error_email_already_in_use)
                            is FirebaseNetworkException -> getString(R.string.error_network_request_failed)
                            is FirebaseAuthInvalidUserException -> getString(R.string.error_user_not_found)
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

    // Функция для обработки регистрации
    private fun signUp() {
        // Обновление значений
        getData()

        // Передача данных в активность
        (activity as? OnSignInListener)?.onSignIn(email, password)
    }

    // Функция для обработки входа гостя
    private fun guest() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            auth.signOut()
        }
        goMain()
    }

    // Переход на главную активность
    private fun goMain() {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Запуск MainActivity
        startActivity(intent)
    }
}