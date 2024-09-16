package com.example.app_kanji.Pesquisar

data class Ideogramas(
    var ex1_significado: String? = null,
    var ex2_significado: String? = null,
    var ex3_significado: String? = null,
    var ex4_significado: String? = null,
    var exemplo1: String? = null,
    var exemplo2: String? = null,
    var exemplo3: String? = null,
    var exemplo4: String? = null,
    var frequencia: Int = 0,
    var imagem: String? = null,
    var kunyomi: String? = null,
    var onyomi: String? = null,
    var qtd_tracos: Int = 0,
    var significado: String? = null,
    val categorias: String? = null
)