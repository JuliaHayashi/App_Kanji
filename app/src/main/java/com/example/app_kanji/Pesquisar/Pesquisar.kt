package com.example.app_kanji.Pesquisar

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.R
import com.google.firebase.database.*

class Pesquisar : Fragment(), KanjiClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pesquisar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        databaseReference = FirebaseDatabase.getInstance().reference.child("Ideogramas")

        setHasOptionsMenu(true)

        val mainFragment = this
        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = CardAdapter(kanjiList, mainFragment)
        }

        populateKanjis()
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(requireContext(), Kanji_InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_pesquisar, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchItem.icon?.setTint(Color.WHITE)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showSearchDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSearchDialog() {
        populateKanjis()
        val searchView = androidx.appcompat.widget.SearchView(requireContext())
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterKanjis(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pesquisar Kanji")
            .setView(searchView)
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                // Quando o diálogo é fechado, exibe todos os kanjis novamente
                (recyclerView.adapter as? CardAdapter)?.updateList(kanjiList)
            }
            .setPositiveButton("Procurar") { dialog, _ ->
                val query = searchView.query.toString()
                if (query.isNotBlank()) {
                    filterKanjis(query) // Chama a função de filtro com a query
                }
            }
            .create()

        dialog.setOnShowListener {
            val searchViewEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchViewEditText.requestFocus()
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }

        dialog.show()
    }


    private fun filterKanjis(query: String) {
        Log.d("FilterKanjis", "Query: $query")

        // Normaliza a query para considerar apenas palavras inteiras, ignorando maiúsculas/minúsculas
        val normalizedQuery = "\\b$query\\b".toRegex(RegexOption.IGNORE_CASE)

        // Filtrar pelo ID (que é o nome do kanji) e pelos significados
        val filteredList = kanjiList.filter { kanji ->
            // Verifica se o ID do kanji corresponde à query
            val matchesId = kanji.id.matches(normalizedQuery)
            // Verifica se algum significado corresponde à query como uma palavra inteira
            val matchesSignificado = kanji.significado?.split(" ")?.any { it.matches(normalizedQuery) } == true

            matchesId || matchesSignificado
        }

        Log.d("FilterKanjis", "Filtered count: ${filteredList.size}")
        (recyclerView.adapter as CardAdapter).updateList(filteredList)
    }

    private fun populateKanjis() {
        kanjiList.clear()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                kanjiList.clear()
                for (ideogramSnapshot in dataSnapshot.children) {
                    val ideogram = ideogramSnapshot.getValue(Ideogramas::class.java)
                    if (ideogram != null) {
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
                        kanjiList.add(kanji)
                    } else {
                        Log.e("FirebaseData", "Erro ao ler o ideograma.")
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
    }

    companion object {}
}