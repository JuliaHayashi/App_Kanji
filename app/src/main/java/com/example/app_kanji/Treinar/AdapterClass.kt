package com.example.app_kanji.Treinar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.R

class AdapterClass(private val dataList: ArrayList<DataClass>): RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvTitle.text = currentItem.dataTitle
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvTitle:TextView = itemView.findViewById(R.id.title)
    }
}