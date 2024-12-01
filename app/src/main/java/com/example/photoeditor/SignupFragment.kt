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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.time.LocalDate

class SignupFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentSignupBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Переменные firebase
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    // Переменные авторизации
    lateinit var username: String
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

        auth = Firebase.auth
        // Получение аргументов
        email = arguments?.getSerializable(ARG_EMAIL).toString()
        password = arguments?.getSerializable(ARG_PASSWORD).toString()

        // Установка обработчика нажатия для кнопки регистрации
        binding.buttonSignUp.setOnClickListener {
            signUp()
        }

        // Установка обработчика нажатия для кнопки возврата
        binding.buttonBack.setOnClickListener {
            back()
        }
    }

    // Функция для обработки регистрации
    @RequiresApi(Build.VERSION_CODES.O)
    private fun signUp() {
        // Обновление значений
        username = binding.editUsername.text.toString()
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()
        // Проверка заполнения
        if (username != "" && email != "" && password != "") {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(FIREDASE_LOG_TAG, "createUserWithEmail:success")
                        val user = hashMapOf(
                            "username" to username,
                            "email" to email,
                            "password" to password,
                            "created_at " to LocalDate.now()
                        )
                        db.collection("Users")
                            .add(user)
                            .addOnSuccessListener { documentReference ->
                                Log.d(FIREDASE_LOG_TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                val cuser = auth.currentUser
                                goMain(cuser)
                            }
                            .addOnFailureListener { e ->
                                Log.w(FIREDASE_LOG_TAG, "Error adding document", e)
                            }
                    } else {
                        Log.w(FIREDASE_LOG_TAG, "createUserWithEmail:failure", task.exception)
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

    // Функция для обработки возврата
    private fun back() {
        // Получение значений
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()
        // Создание экземпляра фрагмента SighinFragment с передачей переменной photo
        val sighinFragment = SighinFragment.newInstance(email, password)
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на SighinFragment
            .replace(R.id.card, sighinFragment)
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }

    // Переход на главную активность
    private fun goMain(user: FirebaseUser?) {
        // Создание Intent для перехода на MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Запуск MainActivity
        startActivity(intent)
    }
}