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
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.widget.ViewPager2
import com.example.photoeditor.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
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
    private var user: FirebaseUser? = null
    private val firebaseLogTag: String = "Firebase_Logs"

    // Списоки фотографий
    private lateinit var userList: MutableList<Photo>
    private lateinit var totalList: MutableList<Photo>

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
        getData()
    }

    // Метод очищения значения из SearchView и сворачиваем его
    private fun cleanSearchView() {
        searchView.post {
            searchView.isIconified = true
            searchView.clearFocus()
        }
    }

    // Метод обновления списков
    @RequiresApi(Build.VERSION_CODES.O)
    fun getData() {

        getPhotosForTotal()
    }

    // Метод получения всех общих фотографий
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPhotosForTotal() {
        totalList = mutableListOf()
        // Чтение данных
        db.collection("Photos")
            .whereEqualTo("private", false)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(firebaseLogTag, "Totals - ${document.id} => ${document.data}")
                    totalList.add(
                        Photo(
                            document.getString("id").toString(),
                            document.getString("created_at").toString(),
                            document.getBoolean("original") == true,
                            document.getString("path").toString(),
                            document.getBoolean("private") == true
                        )
                    )
                }
                if (user == null) {
                    createTabs()
                }
                else {
                    getPhotosForUser()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(firebaseLogTag, "Error getting documents", exception)
            }
    }

    // Метод получения всех фотографий пользователя
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPhotosForUser() {
        userList = mutableListOf()
        val userRef = db.collection("Users").document(user!!.uid)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val photoRefs = document.get("photoRefs") as? List<*>
                    photoRefs?.forEach { photoId ->
                        val photoRef = db.collection("Photos").document(photoId.toString())
                        photoRef.get()
                            .addOnSuccessListener { photoDocument ->
                                if (photoDocument != null) {
                                    Log.d(firebaseLogTag, "Users - ${photoDocument.id} => ${photoDocument.data}")
                                    userList.add(
                                        Photo(
                                            photoDocument.getString("id").toString(),
                                            photoDocument.getString("created_at").toString(),
                                            photoDocument.getBoolean("original") == true,
                                            photoDocument.getString("path").toString(),
                                            photoDocument.getBoolean("private") == true
                                        )
                                    )
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w(firebaseLogTag, "Error getting photo document", e)
                            }
                    }
                    createTabs()
                }
            }
            .addOnFailureListener { e ->
                Log.w(firebaseLogTag, "Error getting user document", e)
            }
    }

    // Метод отображения вкладок
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTabs() {
        // Создаем адаптер для ViewPager2, передавая ему текущий фрагмент и список фотографий
        adapter = if (user == null) {
            PhotoPagerAdapter(this, mutableListOf(), totalList, false)
        } else {
            PhotoPagerAdapter(this, userList, totalList, true)
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
            }
            if (adapter.pageMyPhotoFragment.isVisible) {
                adapter.pageMyPhotoFragment.add()
            }
            if (adapter.pageAllPhotoFragment.isVisible) {
                adapter.pageAllPhotoFragment.add()
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
            }
            if (adapter.pageMyPhotoFragment.isVisible) {
                adapter.pageMyPhotoFragment.delete()
            }
            if (adapter.pageAllPhotoFragment.isVisible) {
                adapter.pageAllPhotoFragment.delete()
            }
        }
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