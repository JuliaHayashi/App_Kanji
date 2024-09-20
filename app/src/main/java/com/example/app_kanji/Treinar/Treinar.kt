package com.example.app_kanji.Treinar

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.R

class Treinar : Fragment(), AdapterClass.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>

    // HashMap para mapear identificadores para nomes legíveis
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
        getData()

        return view
    }

    // Populando os dados de título na lista
    private fun getData() {
        // Iterando pelo HashMap para obter o identificador e o nome legível
        for ((key, value) in titleMap) {
            val dataClass = DataClass(value)  // Adicionando o nome legível à lista
            dataList.add(dataClass)
        }

        val adapter = AdapterClass(dataList, this)
        recyclerView.adapter = adapter
    }

    // Método acionado quando um item da lista é clicado
    override fun onItemClick(title: String) {
        // Encontrar o identificador correspondente ao nome legível
        val categoryId = titleMap.entries.find { it.value == title }?.key

        // Passando o identificador (categoria) para a ListaActivity
        val intent = Intent(activity, ListaActivity::class.java)
        intent.putExtra("categoria", categoryId) // Aqui passamos o identificador correto
        startActivity(intent)
    }
}
