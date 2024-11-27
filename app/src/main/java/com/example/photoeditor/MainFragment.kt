package com.example.photoeditor

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
import java.time.LocalDate

class MainFragment : Fragment() {
    // Инициализация переменной для View Binding
    private lateinit var _binding: FragmentMainBinding
    // Геттер для переменной binding
    private val binding get() = _binding

    // Список фотографий
    private lateinit var photoList: MutableList<Photo>

    // Переменные для работы с вкладками
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

        photoList = buildPhotoList()
        val adapter = PhotoPagerAdapter(this, photoList)
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Общий"
                1 -> "Мой"
                2 -> "Весь"
                else -> ""
            }
        }.attach()

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
            if (adapter.pageTotalPhotoFragment.isVisible) adapter.pageTotalPhotoFragment.add()
            if (adapter.pageMyPhotoFragment.isVisible) adapter.pageMyPhotoFragment.add()
            if (adapter.pageAllPhotoFragment.isVisible) adapter.pageAllPhotoFragment.add()
        }

        // Установка обработчика нажатия для кнопки загрузки
        binding.buttonLoad.setOnClickListener {
            if (adapter.pageTotalPhotoFragment.isVisible) adapter.pageTotalPhotoFragment.load()
            if (adapter.pageMyPhotoFragment.isVisible) adapter.pageMyPhotoFragment.load()
            if (adapter.pageAllPhotoFragment.isVisible) adapter.pageAllPhotoFragment.load()
        }

        // Установка обработчика нажатия для кнопки удаления
        binding.buttonDelete.setOnClickListener {
            if (adapter.pageTotalPhotoFragment.isVisible) adapter.pageTotalPhotoFragment.delete()
            if (adapter.pageMyPhotoFragment.isVisible) adapter.pageMyPhotoFragment.delete()
            if (adapter.pageAllPhotoFragment.isVisible) adapter.pageAllPhotoFragment.delete()
        }
    }

    // Метод для создания списка фотографий
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPhotoList(): MutableList<Photo> {
        return mutableListOf(
            Photo(
                "1",
                "1",
                true,
                "content://media/external/images/media/58742",
                LocalDate.parse("2018-12-12")
            ),
            Photo(
                "2",
                "2",
                true,
                "content://media/external/images/media/58743",
                LocalDate.parse("2019-12-12")
            ),
            Photo(
                "3",
                "3",
                true,
                "content://media/external/images/media/58744",
                LocalDate.parse("2020-12-12")
            ),
            Photo(
                "4",
                "4",
                false,
                "https://i.pinimg.com/originals/3a/dd/56/3add569b9c10105fbda36232e6abb706.jpg",
                LocalDate.parse("2021-12-12")
            ),
            Photo(
                "5",
                "5",
                false,
                "https://i.pinimg.com/originals/2f/dd/a6/2fdda6a89ec49eea3a9818fa20705785.jpg",
                LocalDate.parse("2022-12-12")
            ),
            Photo(
                "6",
                "6",
                false,
                "https://i.pinimg.com/originals/5d/e2/42/5de24294bad21ec99931f4c362354f22.jpg",
                LocalDate.parse("2023-12-12")
            )
        )
    }

    // Метод для отображения информации пользователя
    private fun userInfo() {
        // Начало транзакции фрагмента
        parentFragmentManager.beginTransaction()
            // Замена текущего фрагмента на UserInfoFragment
            .replace(R.id.mainContent, UserInfoFragment())
            // Добавление транзакции в стек обратного вызова
            .addToBackStack(null)
            // Завершение транзакции
            .commit()
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