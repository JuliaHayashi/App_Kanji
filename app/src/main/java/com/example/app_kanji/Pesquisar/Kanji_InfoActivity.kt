package com.example.app_kanji.Pesquisar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.app_kanji.Desenhar.DesenhoActivity
import com.example.app_kanji.R
import com.example.app_kanji.databinding.ActivityKanjiInfoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Kanji_InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKanjiInfoBinding
    private var kanjiList = mutableListOf<Kanji>()
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKanjiInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializando a referência ao Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("Ideogramas")

        // Configura a Toolbar como ActionBar
        setSupportActionBar(binding.toolbar)

        // Ativa o botão de voltar na Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon = getDrawable(R.drawable.baseline_arrow_back_24)?.apply {
            setTint(resources.getColor(android.R.color.white, theme))
        }

        // Obtém o ID como String (o próprio Kanji)
        val kanjiID = intent.getStringExtra(KANJI_ID_EXTRA) ?: ""

        Log.e("Kanji_InfoActivity", "Kanji ID recebido: $kanjiID")

        // Popula a lista de Kanji do Firebase e, ao terminar, busca o Kanji pelo ID
        populateKanjiList {
            val kanji = kanjiFromID(kanjiID)
            if (kanji != null) {
                // Exibe os dados do Kanji usando Glide e os TextViews
                updateUI(kanji)
            } else {
                Log.e("KanjiInfo", "Kanji com ID $kanjiID não encontrado.")
            }
        }

        binding.treinarIcone.setOnClickListener {
            val intent = Intent(this, DesenhoActivity::class.java)
            val kanji = kanjiFromID(kanjiID)
            if (kanji != null) {
                intent.putExtra("KANJI_IMAGE_URL", kanji.imageUrl)
                intent.putExtra("KANJI_ID", kanji.id)
            }
            startActivity(intent)
        }
    }

    // Função para popular a lista de Kanji com dados do Firebase
    private fun populateKanjiList(onDataLoaded: () -> Unit) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kanjiList.clear()
                for (ideogramSnapshot in snapshot.children) {
                    val ideogram = ideogramSnapshot.getValue(Ideogramas::class.java)
                    if (ideogram != null) {
                        val kanji = Kanji(
                            id = ideogramSnapshot.key ?: "",
                            imageUrl = ideogram.imagem ?: "",
                            significado = ideogram.significado,
                            onyomi = ideogram.onyomi,
                            kunyomi = ideogram.kunyomi,
                            qtd_tracos = ideogram.qtd_tracos,
                            frequencia = ideogram.frequencia,
                            exemplo1 = ideogram.exemplo1,
                            ex1_significado = ideogram.ex1_significado,
                            exemplo2 = ideogram.exemplo2,
                            ex2_significado = ideogram.ex2_significado,
                            exemplo3 = ideogram.exemplo3,
                            ex3_significado = ideogram.ex3_significado,
                            exemplo4 = ideogram.exemplo4,
                            ex4_significado = ideogram.ex4_significado
                        )
                        kanjiList.add(kanji)
                    } else {
                        Log.e("FirebaseData", "Erro ao ler o ideograma.")
                    }
                }
                onDataLoaded() // Chama o callback quando os dados são carregados
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
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

    // Função para atualizar a UI com as informações do Kanji
    private fun updateUI(kanji: Kanji) {
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
