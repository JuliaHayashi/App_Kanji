package com.example.app_kanji.Pesquisar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val mainFragment = this
        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = CardAdapter(kanjiList, mainFragment)
        }

        populateKanjis()
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(requireContext(), InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
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
                            ideogram.imagem ?: "", // URL da imagem do Firebase
                            "Significado\n" +
                                    "${ideogram.significado}\n" +
                                    "______________________________________\n" +
                                    "\n" +
                                    "Kunyomi\n" +
                                    "${ideogram.kunyomi}\n" +
                                    "______________________________________\n" +
                                    "\n" +
                                    "Onyomi\n" +
                                    "${ideogram.onyomi}\n" +
                                    "______________________________________\n" +
                                    "\n" +
                                    "Quantidade de Traços\n" +
                                    "${ideogram.qtd_tracos}\n" +
                                    "______________________________________\n" +
                                    "\n" +
                                    "Frequência\n" +
                                    "${ideogram.frequencia}\n" +
                                    "______________________________________\n" +
                                    "\n" +
                                    "${ideogram.exemplo1}\n" +
                                    "${ideogram.ex1_significado}\n" +
                                    "______________________________________\n" +
                                    "\n" +
                                    "${ideogram.exemplo2}\n" +
                                    "${ideogram.ex2_significado}\n" +
                                    "______________________________________\n" +
                                    "\n" +
                                    "${ideogram.exemplo3}\n" +
                                    "${ideogram.ex3_significado}\n" +
                                    "______________________________________\n" +
                                    "\n" +
                                    "${ideogram.exemplo4}\n" +
                                    "${ideogram.ex4_significado}\n" +
                                    "______________________________________\n"
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
