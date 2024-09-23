package com.example.app_kanji.Treinar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.Pesquisar.CardAdapter
import com.example.app_kanji.Pesquisar.KANJI_ID_EXTRA
import com.example.app_kanji.Pesquisar.Kanji
import com.example.app_kanji.Pesquisar.KanjiClickListener
import com.example.app_kanji.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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
        kanjiRecyclerView = findViewById(R.id.recyclerView)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        categoryName = intent.getStringExtra("categoria") ?: ""

        databaseReference = FirebaseDatabase.getInstance()
            .getReference("Categorias")
            .child("DosUsuarios")
            .child(userId)
            .child(categoryName)

        kanjiList = mutableListOf()
        adapter = CardAdapter(kanjiList, this)

        // Usar GridLayoutManager com 3 colunas
        kanjiRecyclerView.layoutManager = GridLayoutManager(this, 3)
        kanjiRecyclerView.adapter = adapter

        saveButton.setOnClickListener {
            val kanji = kanjiInput.text.toString()
            if (kanji.isNotEmpty()) {
                fetchKanjiImageUrl(kanji) { imageUrl ->
                    addKanjiToList(kanji, imageUrl)
                    saveKanjiToDatabase(kanji, imageUrl)
                    kanjiInput.text.clear()
                }
            } else {
                Toast.makeText(this, "Por favor, insira um Kanji", Toast.LENGTH_SHORT).show()
            }
        }

        loadExistingKanjis()
    }

    private fun fetchKanjiImageUrl(kanji: String, callback: (String) -> Unit) {
        val ideogramRef = FirebaseDatabase.getInstance().getReference("Ideogramas").child(kanji)
        ideogramRef.child("imagem").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val imageUrl = task.result?.value as? String ?: ""
                callback(imageUrl)
            } else {
                Log.e("AddKanjisActivity", "Erro ao obter URL da imagem: ${task.exception?.message}")
                callback("URL_DA_IMAGEM_PADRAO") // URL padrão caso falhe
            }
        }
    }

    private fun addKanjiToList(kanji: String, imageUrl: String) {
        kanjiList.add(Kanji(id = kanji, imageUrl = imageUrl, significado = ""))
        adapter.notifyDataSetChanged()
    }

    private fun saveKanjiToDatabase(kanji: String, imageUrl: String) {
        databaseReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val existingKanjis = task.result?.getValue(String::class.java) ?: ""
                val updatedKanjis = if (existingKanjis.isNotEmpty()) {
                    "$existingKanjis, $kanji"
                } else {
                    kanji
                }

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

    private fun loadExistingKanjis() {
        databaseReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val existingKanjis = task.result?.getValue(String::class.java)
                existingKanjis?.split(",")?.forEach { kanji ->
                    fetchKanjiImageUrl(kanji.trim()) { imageUrl ->
                        addKanjiToList(kanji.trim(), imageUrl)
                    }
                }
            } else {
                Toast.makeText(this, "Erro ao carregar Kanjis", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(this, Categoria_InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
    }
}
