package com.example.app_kanji.Pesquisar

var kanjiList = mutableListOf<Kanji>()

val KANJI_ID_EXTRA = "kanjiExtra"

class Kanji(
    var imageUrl: String,
    var significado: String? = null,
    var onyomi: String? = null,
    var kunyomi: String? = null,
    var qtd_tracos: Int = 0,
    var frequencia: Int = 0,
    var exemplo1: String? = null,
    var ex1_significado: String? = null,
    var exemplo2: String? = null,
    var ex2_significado: String? = null,
    var exemplo3: String? = null,
    var ex3_significado: String? = null,
    var exemplo4: String? = null,
    var ex4_significado: String? = null,
    val id: Int? = kanjiList.size
)
