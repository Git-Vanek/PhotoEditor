package com.example.photoeditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.databinding.ActivityPhotoBinding

class PhotoActivity : AppCompatActivity(), ViewPhotoFragment.OnViewPhotoListener, DrawPhotoFragment.OnDrawPhotoListener, FilterPhotoFragment.OnFilterPhotoListener, EditPhotoFragment.OnEditPhotoListener {
    // Инициализация переменной для View Binding
    private lateinit var binding: ActivityPhotoBinding

    // Создание экземпляров фрагментов
    private lateinit var viewPhotoFragment: ViewPhotoFragment
    private lateinit var drawPhotoFragment: DrawPhotoFragment
    private lateinit var filterPhotoFragment: FilterPhotoFragment
    private lateinit var editPhotoFragment: EditPhotoFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка темы для активности
        setTheme(R.style.Theme_PhotoEditor)
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        // Установка макета активности
        setContentView(binding.root)

        // Получение объекта Photo из Intent
        @Suppress("DEPRECATION")
        val photo = intent.getSerializableExtra("photo") as Photo

        // Инициализация фрагментов
        viewPhotoFragment = ViewPhotoFragment.newInstance(photo)
        drawPhotoFragment = DrawPhotoFragment.newInstance(photo)
        filterPhotoFragment = FilterPhotoFragment.newInstance(photo)
        editPhotoFragment = EditPhotoFragment.newInstance(photo)

        // Начало транзакции фрагмента
        supportFragmentManager.beginTransaction()
            // Добавление фрагментов в контейнер
            .add(R.id.imageContent, viewPhotoFragment)
            .add(R.id.imageContent, drawPhotoFragment)
            .add(R.id.imageContent, filterPhotoFragment)
            .add(R.id.imageContent, editPhotoFragment)
            // Скрытие фрагментов
            .hide(drawPhotoFragment)
            .hide(filterPhotoFragment)
            .hide(editPhotoFragment)
            // Завершение транзакции
            .commit()
    }

    override fun onViewPhotoDraw(photo: Photo) {
        // Передача данных в DrawPhotoFragment
        drawPhotoFragment.updateData(photo)
        // Переключение на DrawPhotoFragment
        supportFragmentManager.beginTransaction()
            .hide(viewPhotoFragment)
            .show(drawPhotoFragment)
            .commit()
    }

    override fun onViewPhotoFilter(photo: Photo) {
        // Передача данных в FilterPhotoFragment
        filterPhotoFragment.updateData(photo)
        // Переключение на FilterPhotoFragment
        supportFragmentManager.beginTransaction()
            .hide(viewPhotoFragment)
            .show(filterPhotoFragment)
            .commit()
    }

    override fun onViewPhotoEdit(photo: Photo) {
        // Передача данных в EditPhotoFragment
        editPhotoFragment.updateData(photo)
        // Переключение на EditPhotoFragment
        supportFragmentManager.beginTransaction()
            .hide(viewPhotoFragment)
            .show(editPhotoFragment)
            .commit()
    }

    override fun onDrawPhoto(photo: Photo) {
        // Передача данных в ViewPhotoFragment
        viewPhotoFragment.updateData(photo)
        // Переключение на ViewPhotoFragment
        supportFragmentManager.beginTransaction()
            .hide(drawPhotoFragment)
            .show(viewPhotoFragment)
            .commit()
    }

    override fun onFilterPhoto(photo: Photo) {
        // Передача данных в ViewPhotoFragment
        viewPhotoFragment.updateData(photo)
        // Переключение на ViewPhotoFragment
        supportFragmentManager.beginTransaction()
            .hide(filterPhotoFragment)
            .show(viewPhotoFragment)
            .commit()
    }

    override fun onEditPhoto(photo: Photo) {
        // Передача данных в ViewPhotoFragment
        viewPhotoFragment.updateData(photo)
        // Переключение на ViewPhotoFragment
        supportFragmentManager.beginTransaction()
            .hide(editPhotoFragment)
            .show(viewPhotoFragment)
            .commit()
    }
}