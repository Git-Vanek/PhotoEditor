package com.example.photoeditor

import com.example.photoeditor.databinding.ActivityHelloBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HelloActivity : AppCompatActivity(), SigningFragment.OnSignInListener, SignupFragment.OnSignUpListener {
    // Инициализация переменной для View Binding
    lateinit var binding: ActivityHelloBinding

    // Создание экземпляров фрагментов
    private lateinit var signingFragment: SigningFragment
    private lateinit var signupFragment: SignupFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка темы для активности
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityHelloBinding.inflate(layoutInflater)
        // Установка макета активности
        setContentView(binding.root)

        // Инициализация фрагментов
        signingFragment = SigningFragment.newInstance("", "")
        signupFragment = SignupFragment.newInstance("", "")

        // Начало транзакции фрагмента
        supportFragmentManager.beginTransaction()
            // Добавление фрагментов в контейнер
            .add(R.id.card, signingFragment)
            .add(R.id.card, signupFragment)
            // Скрытие второго фрагмента
            .hide(signupFragment)
            // Завершение транзакции
            .commit()
    }

    override fun onSignIn(email: String, password: String) {
        // Передача данных в SignupFragment
        signupFragment.updateData(email, password)
        // Переключение на SignupFragment
        supportFragmentManager.beginTransaction()
            .hide(signingFragment)
            .show(signupFragment)
            .commit()
    }

    override fun onSignUp(email: String, password: String) {
        // Передача данных в SighingFragment
        signingFragment.updateData(email, password)
        // Переключение на SighingFragment
        supportFragmentManager.beginTransaction()
            .hide(signupFragment)
            .show(signingFragment)
            .commit()
    }
}