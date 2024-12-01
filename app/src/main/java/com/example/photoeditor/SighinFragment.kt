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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SighinFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentSighinBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Переменные firebase
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    // Переменные авторизации
    lateinit var email: String
    lateinit var password: String

    // Тег для догирования
    val FIREDASE_LOG_TAG: String = "Firebase_Logs"

    companion object {
        private const val ARG_EMAIL = "name"
        private const val ARG_PASSWORD = "password"

        fun newInstance(email: String, password: String): SighinFragment {
            val fragment = SighinFragment()
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

        auth = Firebase.auth
        // Получение аргументов
        email = arguments?.getSerializable(ARG_EMAIL).toString()
        password = arguments?.getSerializable(ARG_PASSWORD).toString()

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
        // Обновление значений
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()
        // Проверка заполнения
        if (email != "" && password != "") {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(FIREDASE_LOG_TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        goMain(user)
                    } else {
                        Log.w(FIREDASE_LOG_TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.authentication_failed),
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
        // Получение значений
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()
        // Создание экземпляра фрагмента SignupFragment с передачей переменной photo
        val signupFragment = SignupFragment.newInstance(email, password)
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на SignupFragment
            .replace(R.id.card, signupFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }

    // Функция для обработки входа гостя
    private fun guest() {
        goMain(null)
    }

    // Переход на главную активность
    private fun goMain(user: FirebaseUser?) {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Запуск MainActivity
        startActivity(intent)
    }
}