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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        // Obtém a categoria que foi passada através do Intent
        categoriaSelecionada = intent.getStringExtra("categoria")

        recyclerView = findViewById(R.id.recyclerView)
        databaseReference = FirebaseDatabase.getInstance().reference.child("Ideogramas")

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@ListaActivity, 3)
            adapter = CardAdapter(kanjiList, this@ListaActivity)
        }

        // Popula os Kanjis filtrados pela categoria selecionada
        populateKanjis()
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(this, Kanji_InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
    }

    private fun populateKanjis() {
        kanjiList.clear()

        // Escuta as mudanças no banco de dados
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                kanjiList.clear()

                // Itera por todos os ideogramas no Firebase
                for (ideogramSnapshot in dataSnapshot.children) {
                    val ideogram = ideogramSnapshot.getValue(Ideogramas::class.java)

                    // Aqui filtramos pela categoria selecionada
                    if (ideogram != null && ideogram.categorias == categoriaSelecionada) {
                        Log.d("FirebaseData", "Ideograma: $ideogram")
                        val kanji = Kanji(
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

                        // Adiciona o Kanji à lista filtrada
                        kanjiList.add(kanji)
                    }
                }
                // Notifica o adapter que a lista foi atualizada
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
