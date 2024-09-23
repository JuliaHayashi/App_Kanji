package com.example.app_kanji.Treinar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.MainActivity
import com.example.app_kanji.Pesquisar.CardAdapter
import com.example.app_kanji.Pesquisar.Ideogramas
import com.example.app_kanji.Pesquisar.KANJI_ID_EXTRA
import com.example.app_kanji.Pesquisar.Kanji
import com.example.app_kanji.Pesquisar.KanjiClickListener
import com.example.app_kanji.R
import com.google.firebase.auth.FirebaseAuth
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

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_lista, menu)

        val editItem = menu?.findItem(R.id.edit_list)
        editItem?.icon?.setTint(resources.getColor(R.color.white, theme))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_list -> {
                showEditOptionsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showEditOptionsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha uma ação")
        builder.setItems(arrayOf("Adicionar Kanji", "Renomear Categoria", "Excluir Categoria")) { _, which ->
            when (which) {
                0 -> goToAddKanjiActivity()
                1 -> showRenameCategoryDialog()
                2 -> showDeleteCategoryDialog()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun goToAddKanjiActivity() {
        val intent = Intent(this, AddKanjisActivity::class.java)
        intent.putExtra("categoria", categoriaSelecionada) // Passa a categoria selecionada
        startActivity(intent)
    }

    private fun showRenameCategoryDialog() {
        val input = EditText(this)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Renomear Categoria")
        builder.setView(input)

        builder.setPositiveButton("Renomear") { dialog, _ ->
            val newCategoryName = input.text.toString().trim()
            if (newCategoryName.isNotEmpty()) {
                renameCategory(categoriaSelecionada!!, newCategoryName)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun renameCategory(oldName: String, newName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userCategoryRef = databaseReference.child("Categorias").child("DosUsuarios").child(userId)

        userCategoryRef.child(oldName).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val value = snapshot.value
                userCategoryRef.child(newName).setValue(value).addOnCompleteListener { renameTask ->
                    if (renameTask.isSuccessful) {
                        userCategoryRef.child(oldName).removeValue().addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                Log.d("ListaActivity", "Categoria renomeada de $oldName para $newName")
                            } else {
                                Log.e("ListaActivity", "Erro ao remover a categoria antiga: ${deleteTask.exception?.message}")
                            }
                        }
                    } else {
                        Log.e("ListaActivity", "Erro ao adicionar a nova categoria: ${renameTask.exception?.message}")
                    }
                }
            } else {
                Log.e("ListaActivity", "A categoria antiga não existe.")
            }
        }.addOnFailureListener { exception ->
            Log.e("ListaActivity", "Erro ao acessar a categoria: ${exception.message}")
        }
    }

    private fun showDeleteCategoryDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Excluir Categoria")
        builder.setMessage("Tem certeza que deseja excluir a categoria $categoriaSelecionada?")
        builder.setPositiveButton("Sim") { dialog, _ ->
            deleteCategory(categoriaSelecionada!!)
            dialog.dismiss()
        }
        builder.setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun deleteCategory(title: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userCategoryRef = databaseReference.child("Categorias").child("DosUsuarios").child(userId)

        Log.d("ListaActivity", "Tentando excluir a categoria: $title em ${userCategoryRef.child(title).toString()}")

        userCategoryRef.child(title).get().addOnSuccessListener { snapshot ->
            Log.d("ListaActivity", "Snapshot: ${snapshot.value}") // Log do valor do snapshot
            if (snapshot.exists()) {
                Log.d("ListaActivity", "Categoria encontrada: $title")
                userCategoryRef.child(title).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ListaActivity", "Categoria $title excluída com sucesso.")

                        // Redireciona para a MainActivity com o fragmento "treinar"
                        val intent = Intent(this@ListaActivity, MainActivity::class.java)
                        intent.putExtra("fragment", "treinar") // Passa o nome do fragmento
                        startActivity(intent)
                        finish() // Finaliza a ListaActivity
                    } else {
                        Log.e("ListaActivity", "Erro ao excluir categoria $title: ${task.exception?.message}")
                    }
                }
            } else {
                Log.e("ListaActivity", "Categoria não existe: $title")
            }
        }.addOnFailureListener { exception ->
            Log.e("ListaActivity", "Erro ao acessar a categoria: ${exception.message}")
        }
    }



    override fun onClick(kanji: Kanji) {
        Log.d("ListaActivity", "Kanji selecionado: ${kanji.id}")
        val intent = Intent(this, Categoria_InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
    }

    private fun obterKanjisDaCategoria() {
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
                obterKanjisDaCategoriaDoUsuario()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
    }

    private fun obterKanjisDaCategoriaDoUsuario() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val categoria = categoriaSelecionada ?: run {
            Log.e("CategoriaUsuario", "Categoria selecionada é nula")
            return
        }

        val usuarioRef = databaseReference.child("Categorias").child("DosUsuarios").child(userId).child(categoria)
        Log.d("CategoriaUsuario", "Acessando caminho: Categorias/DosUsuarios/$userId/$categoria")

        usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("CategoriaUsuario", "Dados do snapshot: ${snapshot.value}")

                if (snapshot.exists()) {
                    val kanjisString = snapshot.getValue(String::class.java)
                    if (!kanjisString.isNullOrEmpty()) {
                        kanjisDaCategoria.addAll(kanjisString.split(",").map { it.trim() })
                        Log.d("CategoriaUsuario", "Kanjis da categoria do usuário $categoria: $kanjisDaCategoria")
                    } else {
                        Log.d("CategoriaUsuario", "Nenhum dado encontrado para a categoria do usuário $categoria")
                    }
                } else {
                    Log.d("CategoriaUsuario", "Categoria do usuário não existe: $categoria")
                }
                populateKanjis()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CategoriaUsuario", "Erro ao acessar a categoria do usuário: ${databaseError.message}")
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
