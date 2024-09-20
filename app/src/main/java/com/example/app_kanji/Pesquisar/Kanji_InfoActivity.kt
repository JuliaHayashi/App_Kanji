package com.example.app_kanji.Pesquisar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.app_kanji.R
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
                .placeholder(R.drawable.baseline_info_24)
                .error(R.drawable.baseline_info_24)
                .into(binding.kanjiImage)

            binding.significado.text = kanji.significado
            binding.onyomi.text = kanji.onyomi
            binding.kunyomi.text = kanji.kunyomi
            binding.qtdTracos.text = "${kanji.qtd_tracos}"
            binding.frequencia.text = "${kanji.frequencia}"

            binding.exemplo1.text = kanji.exemplo1
            binding.ex1Significado.text = kanji.ex1_significado

            binding.exemplo2.text = kanji.exemplo2
            binding.ex2Significado.text =kanji.ex2_significado

            binding.exemplo3.text = kanji.exemplo3
            binding.ex3Significado.text = kanji.ex3_significado

            binding.exemplo4.text = kanji.exemplo4
            binding.ex4Significado.text = kanji.ex4_significado
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
