package com.example.photoeditor

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class PhotoWithDatesAdapter(photoAdapter: PhotoAdapter, private val gridCount: Int, private val context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Список элементов (заголовки дат и фотографии)
    private val items = mutableListOf<Any>()

    companion object {
        // Константы для типов элементов
        const val VIEW_TYPE_DATE_HEADER = 0
        const val VIEW_TYPE_PHOTO = 1
    }

    init {
        // Группировка фотографий по месяцам и году и добавление заголовков дат
        val groupedPhotos = photoAdapter.dataset.groupBy { it.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM")) }
        groupedPhotos.keys.forEach { date ->
            items.add(date) // Добавляем заголовок даты
            groupedPhotos[date]?.let { items.add(it) } // Добавляем фотографии для этой даты
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Создание ViewHolder в зависимости от типа элемента
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
        return DateHeaderViewHolder(view, gridCount, context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Привязка данных к ViewHolder в зависимости от типа элемента
        when (getItemViewType(position)) {
            VIEW_TYPE_DATE_HEADER -> {
                val date = items[position] as String
                val photos = items[position + 1] as List<Photo>
                (holder as DateHeaderViewHolder).bind(date, photos)
            }
            else -> {
                // Ничего не делаем, так как фотографии уже отображаются под заголовками даты
            }
        }
    }

    override fun getItemCount(): Int {
        // Возвращает общее количество элементов (заголовки дат + фотографии)
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        // Определение типа элемента (заголовок даты или фотография)
        return if (items[position] is String) VIEW_TYPE_DATE_HEADER else VIEW_TYPE_PHOTO
    }

    class DateHeaderViewHolder(itemView: View, private val gridCount: Int, private val context: Context?) : RecyclerView.ViewHolder(itemView) {
        // TextView для отображения даты
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val photoRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)

        // Привязка данных к TextView и вложенному RecyclerView
        fun bind(date: String, photos: List<Photo>) {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            val localDate = LocalDate.parse(date + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            dateTextView.text = localDate.format(formatter)

            // Установка адаптера и LayoutManager для вложенного RecyclerView
            photoRecyclerView.layoutManager = GridLayoutManager(itemView.context, gridCount)
            photoRecyclerView.adapter = PhotoAdapter(photos.toMutableList(), context) { photo ->
                openPhotoActivity(photo)
            }
        }

        // Метод перехода на новую активность
        private fun openPhotoActivity(photo: Photo) {
            val intent = Intent(context, PhotoActivity::class.java)
            intent.putExtra("photo", photo)
            context?.startActivity(intent)
        }
    }
}