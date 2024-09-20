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

        // Obtém o ID como String (o próprio Kanji)
        val kanjiID = intent.getStringExtra(KANJI_ID_EXTRA) ?: ""

        // Verifica o Kanji a partir do ID
        val kanji = kanjiFromID(kanjiID)
        if (kanji != null) {
            // Exibe os dados do Kanji usando Glide e os TextViews
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
            binding.ex2Significado.text = kanji.ex2_significado

            binding.exemplo3.text = kanji.exemplo3
            binding.ex3Significado.text = kanji.ex3_significado

            binding.exemplo4.text = kanji.exemplo4
            binding.ex4Significado.text = kanji.ex4_significado
        }
    }

    // Método para buscar o Kanji usando ID String
    private fun kanjiFromID(kanjiID: String): Kanji? {
        // Percorre a lista para buscar o Kanji com o ID igual ao próprio nome do ideograma
        for (kanji in kanjiList) {
            if (kanji.id == kanjiID) {
                return kanji
            }
        }
        return null
    }
}