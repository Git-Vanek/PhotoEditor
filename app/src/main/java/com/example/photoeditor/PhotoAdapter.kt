package com.example.photoeditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PhotoAdapter (private var dataset:List<Photo>): RecyclerView.Adapter<PhotoAdapter.ViewHolder>(){
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.image)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_image,viewGroup,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (dataset[position].original == true) {
            viewHolder.image.setImageResource(dataset[position].path.toInt())
        } else {
            Picasso.get()
                .load(dataset[position].path)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(viewHolder.image)
        }
    }
    override fun getItemCount()=dataset.size
    fun filterList(filteredList: List<Photo>) {
        dataset = filteredList
        notifyDataSetChanged()
    }
}