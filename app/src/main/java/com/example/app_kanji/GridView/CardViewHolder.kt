package com.example.app_kanji.GridView

import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.databinding.KanjiCardBinding

class CardViewHolder(
    private val kanjiCardBinding: KanjiCardBinding,
    private val clickListener: KanjiClickListener
) : RecyclerView.ViewHolder(kanjiCardBinding.root)
{
    fun bindBook(kanji: Kanji)
    {
        kanjiCardBinding.cover.setImageResource(kanji.cover)

        kanjiCardBinding.cardView.setOnClickListener{
            clickListener.onClick(kanji)
        }
    }
}