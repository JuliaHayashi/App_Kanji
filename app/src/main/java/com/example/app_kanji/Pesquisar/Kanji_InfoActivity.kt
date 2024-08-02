package com.example.app_kanji.Pesquisar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.app_kanji.R
import com.example.app_kanji.R.*
import com.example.app_kanji.databinding.ActivityKanjiInfoBinding

class Kanji_InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKanjiInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKanjiInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kanjiID = intent.getIntExtra(KANJI_ID_EXTRA, -1)
        val kanji = kanjiFromID(kanjiID)
        if (kanji != null) {
            Glide.with(this)
                .load(kanji.imageUrl)
                .placeholder(drawable.baseline_info_24) // Placeholder enquanto a imagem carrega
                .error(drawable.baseline_info_24) // Imagem de erro
                .into(binding.cover)

            binding.description.text = kanji.description
        }
    }

    private fun kanjiFromID(kanjiID: Int): Kanji? {
        for (kanji in kanjiList) {
            if (kanji.id == kanjiID)
                return kanji
        }
        return null
    }
}
