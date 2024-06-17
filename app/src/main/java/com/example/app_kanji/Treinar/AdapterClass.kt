package com.example.app_kanji.Treinar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.R

class AdapterClass(private val dataList: List<DataClass>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterClass.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(title: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(data: DataClass) {
            titleTextView.text = data.dataTitle
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val title = dataList[position].dataTitle
                itemClickListener.onItemClick(title)
            }
        }
    }
}
