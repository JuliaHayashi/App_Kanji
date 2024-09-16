package com.example.app_kanji.Treinar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.R
import com.example.app_kanji.Pesquisar.Pesquisar // Certifique-se de importar o fragmento correto

class Treinar : Fragment(), AdapterClass.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var titleList: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_treinar, container, false)

        titleList = arrayOf(
            "Numerais",
            "Posições",
            "Adjetivos",
            "Verbos",
            "Dias da semana"
        )

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf()
        getData()

        return view
    }

    private fun getData() {
        for (title in titleList) {
            val dataClass = DataClass(title)
            dataList.add(dataClass)
        }

        val adapter = AdapterClass(dataList, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(title: String) {
        // Substitua o fragmento atual pelo fragmento Pesquisar
        val fragment = Pesquisar()
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // Passando argumentos para o fragmento Pesquisar
        val args = Bundle()
        args.putString("titulo", title)
        fragment.arguments = args

        transaction.replace(R.id.fragment_container, fragment) // Certifique-se de que fragment_container é o ID correto
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
