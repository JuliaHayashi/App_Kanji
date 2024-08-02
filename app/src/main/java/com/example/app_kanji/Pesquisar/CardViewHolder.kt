package com.example.app_kanji.Pesquisar

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_kanji.R
import com.example.app_kanji.databinding.KanjiCardBinding

class CardViewHolder(
    private val kanjiCardBinding: KanjiCardBinding,
    private val clickListener: KanjiClickListener
) : RecyclerView.ViewHolder(kanjiCardBinding.root) {
    fun bindBook(kanji: Kanji) {
        Glide.with(kanjiCardBinding.cover.context)
            .load(kanji.imageUrl)
            .placeholder(R.drawable.baseline_info_24) // Placeholder enquanto a imagem carrega
            .error(R.drawable.baseline_info_24) // Imagem de erro
            .into(kanjiCardBinding.cover)

        kanjiCardBinding.cardView.setOnClickListener {
            clickListener.onClick(kanji)
        }
    }
}
