package com.example.app_kanji.Treinar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.R

class AdapterClass(private val dataList: List<DataClass>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(title: String)
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_EXTRA_SPACE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == dataList.size) VIEW_TYPE_EXTRA_SPACE else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.categoria_layout, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.espaco_vazio_layout, parent, false)
            EmptySpaceViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val data = dataList[position]
            holder.bind(data)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size + 1 // Adiciona 1 para o espaço extra
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

    inner class EmptySpaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
