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

        // Atualiza o caminho para buscar corretamente as categorias criadas pelo usuário
        databaseReference = FirebaseDatabase.getInstance()
            .getReference("Categorias")       // Nó principal de Categorias
            .child("DosUsuarios")             // Subnó para usuários
            .child(userId)                    // Nó do ID do usuário

        // Carrega as categorias do usuário e pré-definidas
        loadCategories()

        val addCategoriaButton: View = view.findViewById(R.id.addCategoria)
        addCategoriaButton.setOnClickListener {
            showAddCategoryDialog()
        }

        return view
    }

    private fun loadCategories() {
        // Escuta as categorias do usuário
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpa a lista antes de carregar as novas categorias para evitar duplicação
                dataList.clear()

                // Adiciona categorias pré-definidas
                for ((key, value) in titleMap) {
                    dataList.add(DataClass(value))
                }

                // Adiciona as categorias do usuário
                for (dataSnapshot in snapshot.children) {
                    val categoryName = dataSnapshot.key
                    categoryName?.let {
                        dataList.add(DataClass(it)) // Adiciona o nome da categoria do usuário
                    }
                }

                // Atualiza o adaptador com as novas categorias
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
        // Obtenha o ID do usuário autenticado
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Verifique se o usuário está autenticado
        if (userId == null) {
            Toast.makeText(requireContext(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }

        // Caminho correto: Categorias -> DosUsuarios -> {userId} -> {categoria}
        val userCategoryRef = FirebaseDatabase.getInstance()
            .getReference("Categorias")       // Nó principal de Categorias
            .child("DosUsuarios")             // Subnó para usuários
            .child(userId)                    // Nó do ID do usuário
            .child(categoryName)              // Nome da categoria específica

        // Verifica se a categoria já existe para o usuário
        userCategoryRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    // Se a categoria já existe, exibe uma mensagem de erro
                    Toast.makeText(requireContext(), "Categoria já existe!", Toast.LENGTH_SHORT).show()
                } else {
                    // Se a categoria não existe, cria a nova categoria
                    userCategoryRef.setValue("") // Adiciona a categoria vazia

                    // Vai para a Activity de adicionar kanjis
                    val intent = Intent(activity, AddKanjisActivity::class.java)
                    intent.putExtra("categoria", categoryName) // Passa o nome da nova categoria
                    startActivity(intent)
                }
            } else {
                Toast.makeText(requireContext(), "Erro ao verificar categoria", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClick(title: String) {
        val categoryId = titleMap.entries.find { it.value == title }?.key ?: title // Permite clicar nas categorias do usuário

        val intent = Intent(activity, ListaActivity::class.java)
        intent.putExtra("categoria", categoryId)
        startActivity(intent)
    }
}
