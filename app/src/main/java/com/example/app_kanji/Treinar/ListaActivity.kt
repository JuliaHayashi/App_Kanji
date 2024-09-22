package com.example.app_kanji.Treinar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.Pesquisar.CardAdapter
import com.example.app_kanji.Pesquisar.Ideogramas
import com.example.app_kanji.Pesquisar.KANJI_ID_EXTRA
import com.example.app_kanji.Pesquisar.Kanji
import com.example.app_kanji.Pesquisar.KanjiClickListener
import com.example.app_kanji.R
import com.google.firebase.database.*

class ListaActivity : AppCompatActivity(), KanjiClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private val kanjiList = mutableListOf<Kanji>()
    private var categoriaSelecionada: String? = null
    private var kanjisDaCategoria: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        categoriaSelecionada = intent.getStringExtra("categoria")

        if (categoriaSelecionada.isNullOrEmpty()) {
            Log.e("ListaActivity", "Categoria inválida ou não passada no Intent.")
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerView)
        databaseReference = FirebaseDatabase.getInstance().reference

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@ListaActivity, 3)
            adapter = CardAdapter(kanjiList, this@ListaActivity)
        }

        obterKanjisDaCategoria()
    }

    override fun onClick(kanji: Kanji) {
        Log.d("ListaActivity", "Kanji selecionado: ${kanji.id}")
        val intent = Intent(this, Categoria_InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
    }

    private fun obterKanjisDaCategoria() {
        // Acessando categorias predefinidas
        val predefinidasRef = databaseReference.child("Categorias").child("Predefinidas").child(categoriaSelecionada!!)
        Log.d("FirebasePath", "Acessando caminho: Categorias/Predefinidas/$categoriaSelecionada")

        predefinidasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val kanjisString = dataSnapshot.getValue(String::class.java)
                Log.d("Categoria", "Dados retornados: $kanjisString")

                if (!kanjisString.isNullOrEmpty()) {
                    kanjisDaCategoria.addAll(kanjisString.split(",").map { it.trim() })
                    Log.d("Categoria", "Kanjis da categoria predefinida $categoriaSelecionada: $kanjisDaCategoria")
                } else {
                    Log.d("Categoria", "Nenhum dado encontrado para a categoria predefinida $categoriaSelecionada")
                }
                obterKanjisUsuarios()  // Chama a função para buscar as categorias dos usuários
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
    }

    private fun obterKanjisUsuarios() {
        // Acessando categorias dos usuários
        val usuariosRef = databaseReference.child("Categorias").child("DosUsuarios")
        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (usuarioSnapshot in dataSnapshot.children) {
                    // Aqui estamos assumindo que você vai pegar todos os kanjis do usuário
                    usuarioSnapshot.child("1").getValue(String::class.java)?.let { kanjisString ->
                        kanjisDaCategoria.addAll(kanjisString.split(",").map { it.trim() })
                        Log.d("Categoria", "Kanjis da categoria do usuário $categoriaSelecionada: $kanjisDaCategoria")
                    }
                }
                // After fetching all kanji, populate the list
                populateKanjis()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
    }
    private fun obterKanjisDeCategoriasDosUsuarios() {
        val usuariosRef = databaseReference.child("Categorias").child("DosUsuarios")

        // Busca os dados de todos os usuários
        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { usuarioSnapshot ->
                    usuarioSnapshot.child(categoriaSelecionada ?: return).getValue(String::class.java)?.split(",")?.map(String::trim)?.let {
                        kanjisDaCategoria.addAll(it)
                        Log.d("Categoria", "Kanjis da categoria do usuário ${usuarioSnapshot.key}: $it")
                    }
                }
                populateKanjis() // Chama a função para atualizar a UI após processar todos os dados
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Erro de banco de dados: ${databaseError.message}")
            }
        })
    }


    private fun populateKanjis() {
        kanjiList.clear()
        databaseReference.child("Ideogramas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ideogramSnapshot in dataSnapshot.children) {
                    val kanjiId = ideogramSnapshot.key ?: continue
                    val ideogram = ideogramSnapshot.getValue(Ideogramas::class.java) ?: continue

                    if (kanjisDaCategoria.contains(kanjiId)) {
                        val kanji = Kanji(
                            id = kanjiId,
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
                        Log.d("KanjiList", "Kanji adicionado: ID = ${kanji.id}")
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
}
