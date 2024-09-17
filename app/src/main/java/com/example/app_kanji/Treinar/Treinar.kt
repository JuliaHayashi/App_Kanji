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
    private lateinit var titleList: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_treinar, container, false)

        // Definindo as categorias de kanjis
        titleList = arrayOf(
            "adjetivos",
            "dias_semana",
            "numerais",
            "posicoes",
            "verbos",
        )

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf()
        getData()

        return view
    }

    // Populando os dados de título na lista
    private fun getData() {
        for (title in titleList) {
            val dataClass = DataClass(title)
            dataList.add(dataClass)
        }

        val adapter = AdapterClass(dataList, this)
        recyclerView.adapter = adapter
    }

    // Método acionado quando um item da lista é clicado
    override fun onItemClick(title: String) {
        // Passando o título/categoria para a ListaActivity
        val intent = Intent(activity, ListaActivity::class.java)
        intent.putExtra("categoria", title) // Modifiquei o parâmetro para "categoria"
        startActivity(intent)
    }
}
