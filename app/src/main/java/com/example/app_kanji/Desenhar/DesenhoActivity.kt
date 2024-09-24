package com.example.app_kanji.Desenhar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.example.app_kanji.R

class DesenhoActivity : AppCompatActivity() {

    private lateinit var desenhoView: DesenhoClass
    private lateinit var backButton: ImageView
    private lateinit var restartButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desenho)

        desenhoView = findViewById(R.id.myDrawingView)
        backButton = findViewById(R.id.backButton)
        restartButton = findViewById(R.id.restartButton)

        backButton.setOnClickListener {
            finish()
        }

        restartButton.setOnClickListener {
            desenhoView.clear()
        }
    }
}
