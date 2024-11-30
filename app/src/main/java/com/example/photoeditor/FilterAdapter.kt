package com.example.photoeditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ja.burhanrashid52.photoeditor.PhotoFilter

class FilterAdapter(private val filters: List<Pair<Int, PhotoFilter>>, private val listener: OnFilterSelectedListener) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    interface OnFilterSelectedListener {
        fun onFilterSelected(filter: PhotoFilter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = filters[position]
        holder.bind(filter)
    }

    override fun getItemCount(): Int = filters.size

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image)

        fun bind(filter: Pair<Int, PhotoFilter>) {
            imageView.setImageResource(filter.first)
            itemView.setOnClickListener {
                listener.onFilterSelected(filter.second)
            }
        }
    }
}