package com.example.app_kanji.Treinar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.app_kanji.R

class ListaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        val titulo = intent.getStringExtra("titulo")

    }
}