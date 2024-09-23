package com.example.app_kanji

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.content.Intent

class DesenhoActivity : AppCompatActivity() {

    private lateinit var desenhoView: DesenhoClass
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desenho)

        desenhoView = findViewById(R.id.myDrawingView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
    }
}
