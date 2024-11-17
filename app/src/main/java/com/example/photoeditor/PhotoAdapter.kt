package com.example.photoeditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PhotoAdapter(private var dataset: List<Photo>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    // Класс ViewHolder для хранения ссылок на элементы интерфейса
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
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
        // Проверка, является ли изображение оригинальным
        if (dataset[position].original) {
            // Установка изображения из ресурсов
            viewHolder.image.setImageResource(dataset[position].path.toInt())
        } else {
            // Загрузка изображения из URL с использованием Picasso
            Picasso.get()
                .load(dataset[position].path)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(viewHolder.image)
        }
    }

    // Метод для получения количества элементов в наборе данных
    override fun getItemCount() = dataset.size

    // Метод для фильтрации списка фотографий
    fun filterList(filteredList: List<Photo>) {
        dataset = filteredList
        notifyDataSetChanged()
    }
}