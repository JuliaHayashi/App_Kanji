package com.example.app_kanji.GridView

var kanjiList = mutableListOf<Kanji>()

val KANJI_ID_EXTRA = "kanjiExtra"

class Kanji(
    var cover: Int,
    var description: String,
    val id: Int? = kanjiList.size
)
