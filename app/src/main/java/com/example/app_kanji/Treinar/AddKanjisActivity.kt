package com.example.app_kanji.Treinar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.Pesquisar.CardAdapter
import com.example.app_kanji.Pesquisar.Kanji
import com.example.app_kanji.Pesquisar.KanjiClickListener
import com.example.app_kanji.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddKanjisActivity : AppCompatActivity(), KanjiClickListener {

    private lateinit var kanjiInput: EditText
    private lateinit var saveButton: Button
    private lateinit var kanjiRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var categoryName: String
    private lateinit var kanjiList: MutableList<Kanji>
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_kanjis)

        kanjiInput = findViewById(R.id.kanjiInput)
        saveButton = findViewById(R.id.saveButton)
        kanjiRecyclerView = findViewById(R.id.kanjiRecyclerView)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        categoryName = intent.getStringExtra("categoria") ?: ""

        // Caminho correto para adicionar kanjis: Categorias/DosUsuarios/{userId}/{categoryName}
        databaseReference = FirebaseDatabase.getInstance()
            .getReference("Categorias")     // Nó principal "Categorias"
            .child("DosUsuarios")           // Subnó "DosUsuarios"
            .child(userId)                  // Nó do ID do usuário
            .child(categoryName)            // Nó da categoria

        kanjiList = mutableListOf()
        adapter = CardAdapter(kanjiList, this)
        kanjiRecyclerView.layoutManager = LinearLayoutManager(this)
        kanjiRecyclerView.adapter = adapter

        saveButton.setOnClickListener {
            val kanji = kanjiInput.text.toString()
            if (kanji.isNotEmpty()) {
                addKanjiToList(kanji)
                saveKanjiToDatabase(kanji)
                kanjiInput.text.clear() // Limpa o campo de entrada após adicionar
            } else {
                Toast.makeText(this, "Por favor, insira um Kanji", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addKanjiToList(kanji: String) {
        kanjiList.add(Kanji(id = kanji, imageUrl = "", significado = "")) // Adiciona à lista de kanjis
        adapter.notifyDataSetChanged()
    }

    private fun saveKanjiToDatabase(kanji: String) {
        databaseReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val existingKanjis = task.result?.getValue(String::class.java) ?: ""
                val updatedKanjis = if (existingKanjis.isNotEmpty()) {
                    "$existingKanjis, $kanji"  // Adiciona o novo kanji à lista existente
                } else {
                    kanji  // Se não existir, apenas adiciona o kanji
                }

                // Atualiza o banco de dados com a nova string de kanjis
                databaseReference.setValue(updatedKanjis).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Toast.makeText(this, "Kanji adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao adicionar Kanji", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Erro ao acessar os kanjis existentes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(kanji: Kanji) {
        // Lógica ao clicar no Kanji
    }
}
