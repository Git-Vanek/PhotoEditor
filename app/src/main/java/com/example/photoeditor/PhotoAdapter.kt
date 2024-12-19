package com.example.photoeditor

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

// Адаптер для RecyclerView, который работает с фотографиями
class PhotoAdapter(var dataset: MutableList<Photo>, private val context: Context?, private val itemClickListener: (Photo) -> Unit) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    // Список выбранных элементов
    private val selectedItems = mutableListOf<Photo>()
    // Флаг режима выбора
    private var isSelectionMode = false

    // Класс ViewHolder для хранения ссылок на элементы интерфейса
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ImageView для отображения изображения
        val image: ImageView = view.findViewById(R.id.image)
        // CheckBox для выбора элемента
        val check: CheckBox = view.findViewById(R.id.checkbox)
    }

    // Метод для создания новых элементов интерфейса
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Создание нового представления
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_image, viewGroup, false)
        return ViewHolder(view)
    }

    // Метод для привязки данных к элементу интерфейса
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (position >= dataset.size) {
            return
        }

        val photo = dataset[position]

        // Проверка, является ли изображение оригинальным
        if (photo.original) {
            // Загрузка изображения с устройства
            Picasso.get()
                .load(Uri.parse(photo.path))
                .error(R.drawable.ic_launcher_background) // Изображение для ошибки
                .placeholder(R.drawable.ic_launcher_foreground) // Заглушка
                .into(viewHolder.image, object : Callback {
                    override fun onSuccess() {
                        // Изображение успешно загружено
                        // Обработка нажатия на изображение
                        viewHolder.image.setOnClickListener {
                            // Переход на просмотр изображения
                            itemClickListener(photo)
                        }
                    }

                    override fun onError(e: Exception?) {
                        // Произошла ошибка при загрузке изображения
                        // Обработка нажатия на изображение
                        viewHolder.image.setOnClickListener {
                            // Вывод сообщения с ошибкой
                            Toast.makeText(
                                context,
                                "The photo has not been uploaded.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        } else {
            // Загрузка изображения из интернета
            Picasso.get()
                .load(photo.path)
                .error(R.drawable.ic_launcher_background) // Изображение для ошибки
                .placeholder(R.drawable.ic_launcher_foreground) // Заглушка
                .into(viewHolder.image, object : Callback {
                    override fun onSuccess() {
                        // Изображение успешно загружено
                        // Обработка нажатия на изображение
                        viewHolder.image.setOnClickListener {
                            // Переход на просмотр изображения
                            itemClickListener(photo)
                        }
                    }

                    override fun onError(e: Exception?) {
                        // Произошла ошибка при загрузке изображения
                        // Обработка нажатия на изображение
                        viewHolder.image.setOnClickListener {
                            // Вывод сообщения с ошибкой
                            Toast.makeText(
                                context,
                                "The photo has not been uploaded.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        }

        // Установка состояния CheckBox в зависимости от выбранных элементов
        viewHolder.check.isChecked = selectedItems.contains(photo)
        // Установка видимости CheckBox в зависимости от режима выбора
        viewHolder.check.visibility = if (isSelectionMode) View.VISIBLE else View.GONE

        // Обработка долгого нажатия на изображение для переключения режима выбора
        viewHolder.image.setOnLongClickListener {
            isSelectionMode = !isSelectionMode
            // Обновление только текущего элемента
            notifyItemChanged(position)
            true
        }

        // Обработка изменения состояния CheckBox
        viewHolder.check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(photo)
            } else {
                selectedItems.remove(photo)
            }
        }
    }

    // Метод для получения количества элементов в наборе данных
    override fun getItemCount() = dataset.size

    // Метод для получения списка выбранных элементов
    fun getSelectedItems(): MutableList<Photo> {
        return selectedItems
    }

    // Метод для фильтрации списка фотографий
    fun filterList(filteredList: MutableList<Photo>) {
        dataset = filteredList
        notifyDataSetChanged()
    }
}