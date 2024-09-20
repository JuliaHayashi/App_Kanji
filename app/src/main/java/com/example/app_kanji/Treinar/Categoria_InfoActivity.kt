package com.example.app_kanji.Treinar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.app_kanji.Pesquisar.Ideogramas
import com.example.app_kanji.Pesquisar.KANJI_ID_EXTRA
import com.example.app_kanji.R
import com.example.app_kanji.databinding.ActivityKanjiInfoBinding
import com.google.firebase.database.*

class Categoria_InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKanjiInfoBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKanjiInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializando a referência ao Firebase
        databaseReference = FirebaseDatabase.getInstance().reference

        // Obtém o ID do Kanji como String (o próprio ideograma)
        val kanjiID = intent.getStringExtra(KANJI_ID_EXTRA) ?: ""

        if (kanjiID.isNotEmpty()) {
            // Chama a função para buscar o Kanji no Firebase
            obterKanjiDoFirebase(kanjiID)
        } else {
            // Log de erro ou mensagem de erro na interface, caso o ID seja inválido
            binding.significado.text = "Erro: ID do Kanji inválido."
        }
    }

    private fun obterKanjiDoFirebase(kanjiID: String) {
        // Busca o Kanji no Firebase usando o ID (que é o nome do ideograma)
        val kanjiRef = databaseReference.child("Ideogramas").child(kanjiID)

        kanjiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtém os dados do Kanji como objeto Ideogramas
                val ideogram = dataSnapshot.getValue(Ideogramas::class.java)

                if (ideogram != null) {
                    // Preenche a interface com os dados do Kanji
                    atualizarUIComKanji(ideogram)
                } else {
                    // Se não houver dados, exibe mensagem de erro
                    binding.significado.text = "Erro: Kanji não encontrado."
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Em caso de erro no Firebase
                binding.significado.text = "Erro ao acessar o banco de dados."
            }
        })
    }

    private fun atualizarUIComKanji(kanji: Ideogramas) {
        // Exibe os dados do Kanji usando Glide e os TextViews
        Glide.with(this)
            .load(kanji.imagem)
            .placeholder(R.drawable.baseline_info_24)
            .error(R.drawable.baseline_info_24)
            .into(binding.kanjiImage)

        binding.significado.text = kanji.significado
        binding.onyomi.text = kanji.onyomi
        binding.kunyomi.text = kanji.kunyomi
        binding.qtdTracos.text = "${kanji.qtd_tracos}"
        binding.frequencia.text = "${kanji.frequencia}"

        binding.exemplo1.text = kanji.exemplo1
        binding.ex1Significado.text = kanji.ex1_significado

        binding.exemplo2.text = kanji.exemplo2
        binding.ex2Significado.text = kanji.ex2_significado

        binding.exemplo3.text = kanji.exemplo3
        binding.ex3Significado.text = kanji.ex3_significado

        binding.exemplo4.text = kanji.exemplo4
        binding.ex4Significado.text = kanji.ex4_significado
    }
}
