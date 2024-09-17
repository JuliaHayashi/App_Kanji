package com.example.app_kanji.Treinar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.Pesquisar.CardAdapter
import com.example.app_kanji.Pesquisar.Ideogramas
import com.example.app_kanji.Pesquisar.Kanji
import com.example.app_kanji.Pesquisar.KanjiClickListener
import com.example.app_kanji.Pesquisar.Kanji_InfoActivity
import com.example.app_kanji.R
import com.google.firebase.database.*

class ListaActivity : AppCompatActivity(), KanjiClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private val kanjiList = mutableListOf<Kanji>()
    private var categoriaSelecionada: String? = null
    private var kanjisDaCategoria: List<String> = emptyList() // Lista de Kanjis da categoria selecionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        // Obtém a categoria que foi passada através do Intent
        categoriaSelecionada = intent.getStringExtra("categoria")

        // Verifica se a categoria foi passada corretamente, se não, encerra a Activity
        if (categoriaSelecionada.isNullOrEmpty()) {
            Log.e("ListaActivity", "Categoria inválida ou não passada no Intent.")
            finish() // Encerra a Activity se a categoria for inválida
            return
        }

        recyclerView = findViewById(R.id.recyclerView)
        databaseReference = FirebaseDatabase.getInstance().reference

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@ListaActivity, 3)
            adapter = CardAdapter(kanjiList, this@ListaActivity)
        }

        // Primeiro, busca os Kanjis da categoria selecionada
        obterKanjisDaCategoria()
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(this, Kanji_InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
    }

    private fun obterKanjisDaCategoria() {
        val categoriasRef = databaseReference.child("Categorias").child("Predefinidas")

        categoriasRef.child(categoriaSelecionada!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val kanjisString = dataSnapshot.getValue(String::class.java)
                if (kanjisString != null) {
                    kanjisDaCategoria = kanjisString.split(", ").map { it.trim() }
                    Log.d("Categoria", "Kanjis da categoria $categoriaSelecionada: $kanjisDaCategoria")
                    populateKanjis()
                } else {
                    Log.d("Categoria", "Nenhum dado encontrado para a categoria $categoriaSelecionada")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
    }

    private fun populateKanjis() {
        kanjiList.clear()

        databaseReference.child("Ideogramas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                kanjiList.clear()

                for (ideogramSnapshot in dataSnapshot.children) {
                    // Obtém o ID do nó que é o Kanji
                    val kanjiId = ideogramSnapshot.key

                    // Obtém os dados do Ideograma
                    val ideogram = ideogramSnapshot.getValue(Ideogramas::class.java)

                    if (ideogram != null && kanjiId != null) {
                        Log.d("Ideogram", "Kanji ID: $kanjiId, Kanjis da Categoria: $kanjisDaCategoria")

                        // Verifica se o Kanji ID está na lista de Kanjis da Categoria
                        if (kanjisDaCategoria.contains(kanjiId)) {
                            val kanji = Kanji(
                                imageUrl = ideogram.imagem ?: "",
                                significado = ideogram.significado ?: "",
                                onyomi = ideogram.onyomi ?: "",
                                kunyomi = ideogram.kunyomi ?: "",
                                qtd_tracos = ideogram.qtd_tracos ?: 0,
                                frequencia = ideogram.frequencia ?: 0,
                                exemplo1 = ideogram.exemplo1 ?: "",
                                ex1_significado = ideogram.ex1_significado ?: "",
                                exemplo2 = ideogram.exemplo2 ?: "",
                                ex2_significado = ideogram.ex2_significado ?: "",
                                exemplo3 = ideogram.exemplo3 ?: "",
                                ex3_significado = ideogram.ex3_significado ?: "",
                                exemplo4 = ideogram.exemplo4 ?: "",
                                ex4_significado = ideogram.ex4_significado ?: ""
                            )

                            kanjiList.add(kanji)
                        }
                    } else {
                        Log.d("Ideogram", "Ideograma ou Kanji ID nulo detectado.")
                    }
                }

                Log.d("KanjiList", "Número de Kanjis encontrados: ${kanjiList.size}")
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
    }


    companion object {
        const val KANJI_ID_EXTRA = "KANJI_ID_EXTRA"
    }
}
