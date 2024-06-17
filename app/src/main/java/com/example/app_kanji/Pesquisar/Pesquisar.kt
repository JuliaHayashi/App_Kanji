package com.example.app_kanji.Pesquisar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.R

class Pesquisar : Fragment(), KanjiClickListener {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_pesquisar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)

        populateKanjis()

        val mainFragment = this
        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = CardAdapter(kanjiList, mainFragment)
        }
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(requireContext(), InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
    }

    private fun populateKanjis() {

        kanjiList.clear()

        val kanji1 = Kanji(
            R.drawable.hi,
            "Significado\n" +
                    "Dia, Sol, Contador de anos\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ひ, -び, -か\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ニチ, ジツ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "4\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "1\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"明日あなたは何をするのですか。\"\n" +
                    "O que você vai fazer amanhã?\n" +
                    "______________________________________\n" +
                    "\n" +
                    "日本の中であなたはどこに行きたいですか？\n" +
                    "Onde você quer ir no Japão?\n" +
                    "______________________________________\n"

        )

        kanjiList.add(kanji1)

        val kanji2 = Kanji(
            R.drawable.ichi,
            "Significado\n" +
                    "Um, 1\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ひと(つ)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "イチ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "1\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"一人で学校へ行きます。\"\n" +
                    "\n" +
                    "Eu vou para a escola sozinho.\n" +
                    "______________________________________\n" +
                    "\n" +
                    "一週間ずっと日本語を勉強しました。\n" +
                    "Estudei japonês a semana toda.\n" +
                    "______________________________________\n"
        )

        kanjiList.add(kanji2)

        val kanji3 = Kanji(
            R.drawable.ni,
            "Significado\n" +
                    "Dois, 2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ふた(つ)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ニ、 ジ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "8\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"水を二つください！\"\n" +
                    "\n" +
                    "Duas águas, por favor!\n" +
                    "______________________________________\n" +
                    "\n" +
                    "二月はとても寒いです。\n" +
                    "Fevereiro é muito frio.\n" +
                    "______________________________________\n"
            )
        kanjiList.add(kanji3)

        val kanji4 = Kanji(
            R.drawable.hito,
            "Significado\n" +
                    "Pessoa\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ひと\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ジン、 ニン\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "4\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"一人で学校へ行きます。\"\n" +
                    "\n" +
                    "Eu vou para a escola sozinho.\n" +
                    "______________________________________\n" +
                    "\n" +
                    "私はアメリカ人です。\n" +
                    "Eu sou americano.\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji4)

        val kanji5 = Kanji(
            R.drawable.dai,
            "Significado\n" +
                    "Dois, 2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "おお(きい)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ダイ、 タイ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "6\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"あなたのことが大好きだ。\"\n" +
                    "\n" +
                    "Eu te amo muito.\n" +
                    "______________________________________\n" +
                    "\n" +
                    "もう少し大きな声で話してください。\n" +
                    "Por favor, fale um pouco mais alto.\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji5)

        val kanji6 = Kanji(
            R.drawable.koku,
            "Significado\n" +
                    "Cidade\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "くに\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "コク\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "7\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "3\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"あなたはどこの国から来ましたか？！\"\n" +
                    "\n" +
                    "De que país você é?\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji6)

        val kanji7 = Kanji(
            R.drawable.nen,
            "Significado\n" +
                    "Dois, 2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ふた(つ)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ニ、 ジ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "8\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"水を二つください！\"\n" +
                    "\n" +
                    "Duas águas, por favor!\n" +
                    "______________________________________\n" +
                    "\n" +
                    "二月はとても寒いです。\n" +
                    "Fevereiro é muito frio.\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji7)

        val kanji8 = Kanji(
            R.drawable.juu,
            "Significado\n" +
                    "Dois, 2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ふた(つ)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ニ、 ジ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "8\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"水を二つください！\"\n" +
                    "\n" +
                    "Duas águas, por favor!\n" +
                    "______________________________________\n" +
                    "\n" +
                    "二月はとても寒いです。\n" +
                    "Fevereiro é muito frio.\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji8)

        val kanji9 = Kanji(
            R.drawable.hon,
            "Significado\n" +
                    "Dois, 2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ふた(つ)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ニ、 ジ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "8\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"水を二つください！\"\n" +
                    "\n" +
                    "Duas águas, por favor!\n" +
                    "______________________________________\n" +
                    "\n" +
                    "二月はとても寒いです。\n" +
                    "Fevereiro é muito frio.\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji9)

        val kanji10 = Kanji(
            R.drawable.naka,
            "Significado\n" +
                    "Dois, 2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ふた(つ)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ニ、 ジ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "8\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"水を二つください！\"\n" +
                    "\n" +
                    "Duas águas, por favor!\n" +
                    "______________________________________\n" +
                    "\n" +
                    "二月はとても寒いです。\n" +
                    "Fevereiro é muito frio.\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji10)

        val kanji11 = Kanji(
            R.drawable.nagai,
            "Significado\n" +
                    "Dois, 2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ふた(つ)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ニ、 ジ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "8\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"水を二つください！\"\n" +
                    "\n" +
                    "Duas águas, por favor!\n" +
                    "______________________________________\n" +
                    "\n" +
                    "二月はとても寒いです。\n" +
                    "Fevereiro é muito frio.\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji11)

        val kanji12 = Kanji(
            R.drawable.dasu,
            "Significado\n" +
                    "Dois, 2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Kunyomi\n" +
                    "ふた(つ)\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Onyomi\n" +
                    "ニ、 ジ\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Quantidade de Traços\n" +
                    "2\n" +
                    "______________________________________\n" +
                    "\n" +
                    "Frequência\n" +
                    "8\n" +
                    "______________________________________\n" +
                    "\n" +
                    "\"水を二つください！\"\n" +
                    "\n" +
                    "Duas águas, por favor!\n" +
                    "______________________________________\n" +
                    "\n" +
                    "二月はとても寒いです。\n" +
                    "Fevereiro é muito frio.\n" +
                    "______________________________________\n"
        )
        kanjiList.add(kanji12)
    }

    companion object {}
}