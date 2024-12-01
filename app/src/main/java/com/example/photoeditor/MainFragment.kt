package com.example.photoeditor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.widget.ViewPager2
import com.example.photoeditor.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentMainBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Переменные firebase
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private val firebaseLogTag: String = "Firebase_Logs"

    // Список фотографий
    private lateinit var photoList: MutableList<Photo>

    // Строка поиска
    private lateinit var searchView: SearchView

    // Переменные для работы с вкладками
    private lateinit var adapter: PhotoPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация View Binding для макета фрагмента
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        // Возвращение корневого элемента макета
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Получение пользователя
        user = Firebase.auth.currentUser
        // Создание вкладок
        createTabs()

        // Настройка SearchView
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Логика для обработки отправки запроса
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Логика для фильтрации списка при изменении текста
                if (newText != null) {
                    if (adapter.pageTotalPhotoFragment.isVisible) adapter.pageTotalPhotoFragment.filterList(newText)
                    if (adapter.pageMyPhotoFragment.isVisible) adapter.pageMyPhotoFragment.filterList(newText)
                    if (adapter.pageAllPhotoFragment.isVisible) adapter.pageAllPhotoFragment.filterList(newText)
                }
                return true
            }
        })

        // Регистрируем обратный вызов для отслеживания изменений страницы в ViewPager2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            // Этот метод вызывается, когда выбранная страница изменяется
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // В зависимости от позиции выбранной страницы выполняем соответствующие действия
                when (position) {
                    0 -> cleanSearchView()
                    1 -> cleanSearchView()
                    2 -> cleanSearchView()
                }
            }
        })

        // Установка обработчика нажатия для кнопки информация пользователя
        binding.buttonUserInfo.setOnClickListener {
            userInfo()
        }

        // Установка обработчика нажатия для кнопки настройки
        binding.buttonSettings.setOnClickListener {
            settings()
        }

        // Установка обработчика нажатия для кнопки добавления
        binding.buttonAdd.setOnClickListener {
            if (adapter.pageTotalPhotoFragment.isVisible) {
                adapter.pageTotalPhotoFragment.add()
                createTabs()
            }
            if (adapter.pageMyPhotoFragment.isVisible) {
                adapter.pageMyPhotoFragment.add()
                createTabs()
            }
            if (adapter.pageAllPhotoFragment.isVisible) {
                adapter.pageAllPhotoFragment.add()
                createTabs()
            }
        }

        // Установка обработчика нажатия для кнопки загрузки
        binding.buttonLoad.setOnClickListener {
            if (adapter.pageTotalPhotoFragment.isVisible) {
                adapter.pageTotalPhotoFragment.load()
            }
            if (adapter.pageMyPhotoFragment.isVisible) {
                adapter.pageMyPhotoFragment.load()
            }
            if (adapter.pageAllPhotoFragment.isVisible) {
                adapter.pageAllPhotoFragment.load()
            }
        }

        // Установка обработчика нажатия для кнопки удаления
        binding.buttonDelete.setOnClickListener {
            if (adapter.pageTotalPhotoFragment.isVisible) {
                adapter.pageTotalPhotoFragment.delete()
                createTabs()
            }
            if (adapter.pageMyPhotoFragment.isVisible) {
                adapter.pageMyPhotoFragment.delete()
                createTabs()
            }
            if (adapter.pageAllPhotoFragment.isVisible) {
                adapter.pageAllPhotoFragment.delete()
                createTabs()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTabs() {
        // Создаем список фотографий
        photoList = buildPhotoList()
        // Создаем адаптер для ViewPager2, передавая ему текущий фрагмент и список фотографий
        adapter = if (user == null) {
            PhotoPagerAdapter(this, photoList, false)
        } else {
            PhotoPagerAdapter(this, photoList, true)
        }
        // Инициализируем TabLayout из разметки
        tabLayout = binding.tabLayout
        // Инициализируем ViewPager2 из разметки
        viewPager = binding.viewPager
        // Устанавливаем адаптер для ViewPager2
        viewPager.adapter = adapter
        // Создаем TabLayoutMediator для синхронизации TabLayout и ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Устанавливаем текст для каждой вкладки в зависимости от позиции
            tab.text = when (position) {
                0 -> getString(R.string.general) // Текст для первой вкладки
                1 -> getString(R.string.my) // Текст для второй вкладки
                2 -> getString(R.string.all) // Текст для третьей вкладки
                else -> "" // Текст для других позиций (если есть)
            }
        }.attach() // Присоединяем TabLayoutMediator к TabLayout и ViewPager2
    }

    // Метод очищения значения из SearchView и сворачиваем его
    private fun cleanSearchView() {
        searchView.post {
            searchView.isIconified = true
            searchView.clearFocus()
        }
    }

    // Метод для создания списка фотографий
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPhotoList(): MutableList<Photo> {
        return mutableListOf(

        )
    }

    // Метод для отображения информации пользователя
    private fun userInfo() {
        if (user == null) {
            // Создание Intent для перехода на HelloActivity
            val intent = Intent(activity, HelloActivity::class.java)
            // Запуск HelloActivity
            startActivity(intent)
        }
        else {
            // Начало транзакции фрагмента
            parentFragmentManager.beginTransaction()
                // Замена текущего фрагмента на UserInfoFragment
                .replace(R.id.mainContent, UserInfoFragment())
                // Добавление транзакции в стек обратного вызова
                .addToBackStack(null)
                // Завершение транзакции
                .commit()
        }
    }

    // Метод для отображения настроек
    private fun settings() {
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на SettingsFragment
            .replace(R.id.mainContent, SettingsFragment())
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
    }
}