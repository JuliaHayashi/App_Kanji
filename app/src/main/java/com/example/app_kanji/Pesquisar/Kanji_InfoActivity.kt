package com.example.app_kanji.Pesquisar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.app_kanji.Desenhar.DesenhoActivity
import com.example.app_kanji.R
import com.example.app_kanji.databinding.ActivityKanjiInfoBinding

class Kanji_InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKanjiInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKanjiInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a Toolbar como ActionBar
        setSupportActionBar(binding.toolbar)

        // Ativa o botão de voltar na Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon = getDrawable(R.drawable.baseline_arrow_back_24)?.apply {
            setTint(resources.getColor(android.R.color.white, theme))
        }

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
        binding.treinarIcone.setOnClickListener {
            val intent = Intent(this, DesenhoActivity::class.java)
            if (kanji != null) {
                intent.putExtra("KANJI_IMAGE_URL", kanji.imageUrl)
            }
            startActivity(intent)
        }

        val receivedKanjiID = intent.getStringExtra("id")

        if (receivedKanjiID != null) {
            // Se o ID do Kanji foi recebido, faz a busca pelas informações do Kanji
            val kanjis = kanjiFromID(receivedKanjiID.toString())

            if (kanjis != null) {
                // Exibe os dados do Kanji usando Glide e os TextViews
                Glide.with(this)
                    .load(kanjis.imageUrl)
                    .placeholder(R.drawable.baseline_info_24)
                    .error(R.drawable.baseline_info_24)
                    .into(binding.kanjiImage)

                binding.significado.text = kanjis.significado
                binding.onyomi.text = kanjis.onyomi
                binding.kunyomi.text = kanjis.kunyomi
                binding.qtdTracos.text = "${kanjis.qtd_tracos}"
                binding.frequencia.text = "${kanjis.frequencia}"

                binding.exemplo1.text = kanjis.exemplo1
                binding.ex1Significado.text = kanjis.ex1_significado

                binding.exemplo2.text = kanjis.exemplo2
                binding.ex2Significado.text = kanjis.ex2_significado

                binding.exemplo3.text = kanjis.exemplo3
                binding.ex3Significado.text = kanjis.ex3_significado

                binding.exemplo4.text = kanjis.exemplo4
                binding.ex4Significado.text = kanjis.ex4_significado
            } else {
                binding.significado.text = "Kanji não encontrado."
            }

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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finaliza a Activity atual e volta para o fragment anterior
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}