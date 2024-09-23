package com.example.app_kanji.Treinar

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Treinar : Fragment(), AdapterClass.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: AdapterClass
    private lateinit var userId: String

    private val titleMap = hashMapOf(
        "adjetivos" to "Adjetivos",
        "dias_semana" to "Dias da Semana",
        "numerais" to "Numerais",
        "posicoes" to "Posições",
        "verbos" to "Verbos"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_treinar, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        databaseReference = FirebaseDatabase.getInstance()
            .getReference("Categorias")
            .child("DosUsuarios")
            .child(userId)

        loadCategories()

        val addCategoriaButton: View = view.findViewById(R.id.addCategoria)
        addCategoriaButton.setOnClickListener {
            showAddCategoryDialog()
        }

        return view
    }

    private fun loadCategories() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                // Adiciona categorias pré-definidas
                for ((key, value) in titleMap) {
                    dataList.add(DataClass(value))
                }

                // Adiciona as categorias do usuário
                for (dataSnapshot in snapshot.children) {
                    val categoryName = dataSnapshot.key
                    categoryName?.let {
                        dataList.add(DataClass(it))
                    }
                }

                adapter = AdapterClass(dataList, this@Treinar)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddCategoryDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Adicionar Nova Categoria")

        val input = EditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("Adicionar") { dialog, _ ->
            val newCategory = input.text.toString()
            if (newCategory.isNotEmpty()) {
                addCategoryToDatabase(newCategory)
            } else {
                Toast.makeText(requireContext(), "Nome da categoria não pode ser vazio", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun addCategoryToDatabase(categoryName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Verifique se o usuário está autenticado
        if (userId == null) {
            Toast.makeText(requireContext(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }

        val forbiddenCategories = listOf("Adjetivos", "Dias da Semana", "Numerais", "Posições", "Verbos")
        val trimmedCategoryName = categoryName.trim()

        if (forbiddenCategories.contains(trimmedCategoryName)) {
            Toast.makeText(requireContext(), "Categoria não pode ser um dos nomes reservados!", Toast.LENGTH_SHORT).show()
            return
        }

        val userCategoryRef = FirebaseDatabase.getInstance()
            .getReference("Categorias")
            .child("DosUsuarios")
            .child(userId)
            .child(trimmedCategoryName)

        userCategoryRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    Toast.makeText(requireContext(), "Categoria já existe!", Toast.LENGTH_SHORT).show()
                } else {
                    userCategoryRef.setValue("")
                    val intent = Intent(activity, AddKanjisActivity::class.java)
                    intent.putExtra("categoria", trimmedCategoryName) // Passa o nome da nova categoria
                    startActivity(intent)
                }
            } else {
                Toast.makeText(requireContext(), "Erro ao verificar categoria", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClick(title: String) {
        val categoryId = titleMap.entries.find { it.value == title }?.key ?: title

        val intent = Intent(activity, ListaActivity::class.java)
        intent.putExtra("categoria", categoryId)
        startActivity(intent)
    }
}
