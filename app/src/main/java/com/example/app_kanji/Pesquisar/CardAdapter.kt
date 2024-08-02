package com.example.app_kanji.Pesquisar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.databinding.KanjiCardBinding

class CardAdapter(
    private val kanjis: List<Kanji>,
    private val clickListener: KanjiClickListener
) : RecyclerView.Adapter<CardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = KanjiCardBinding.inflate(from, parent, false)
        return CardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindBook(kanjis[position])
    }

    override fun getItemCount(): Int = kanjis.size
}
