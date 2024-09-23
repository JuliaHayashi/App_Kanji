package com.example.app_kanji

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView

class DesenhoActivity : AppCompatActivity() {

    private lateinit var desenhoView: DesenhoClass
    private lateinit var resetButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desenho)

        desenhoView = findViewById(R.id.myDrawingView)
        resetButton = findViewById(R.id.backButton)

        resetButton.setOnClickListener {
            desenhoView.clear()
        }
    }
}
