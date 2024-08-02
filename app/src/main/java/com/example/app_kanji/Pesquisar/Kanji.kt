package com.example.app_kanji.Pesquisar

var kanjiList = mutableListOf<Kanji>()

val KANJI_ID_EXTRA = "kanjiExtra"

class Kanji(
    var imageUrl: String,
    var description: String,
    val id: Int? = kanjiList.size
)
