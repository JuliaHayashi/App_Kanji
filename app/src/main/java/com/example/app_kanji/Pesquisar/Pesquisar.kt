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
        val intent = Intent(requireContext(), Kanji_InfoActivity::class.java)
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
