package com.example.app_kanji.Desenhar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.app_kanji.R

class DesenhoActivity : AppCompatActivity() {

    private lateinit var desenhoView: DesenhoClass
    private lateinit var backButton: ImageView
    private lateinit var restartButton: ImageView
    private lateinit var kanjiImageView: ImageView
    private lateinit var toggleImageIcon: ImageView

    private var isImageVisible: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desenho)

        desenhoView = findViewById(R.id.myDrawingView)
        backButton = findViewById(R.id.backButton)
        restartButton = findViewById(R.id.restartButton)
        kanjiImageView = findViewById(R.id.kanjiImage)
        toggleImageIcon = findViewById(R.id.toggleImageIcon)

        backButton.setOnClickListener {
            finish()
        }

        restartButton.setOnClickListener {
            desenhoView.clear()
        }

        val animationButton = findViewById<ImageView>(R.id.animationButton)

        animationButton.setOnClickListener {
            val intent = Intent(this, AnimationActivity::class.java)
            intent.putExtra("KANJI_SVG_RESOURCE_ID", R.raw.u4e00) // Substitua 'seu_svg' pelo nome do seu arquivo SVG
            startActivity(intent)

        }


        // Recebe a URL da imagem do kanji da atividade anterior
        val kanjiImageUrl = intent.getStringExtra("KANJI_IMAGE_URL")

        if (kanjiImageUrl != null) {
            // Usa Glide para carregar a imagem do kanji no ImageView
            Glide.with(this)
                .load(kanjiImageUrl)
                .placeholder(R.drawable.baseline_info_24)
                .error(R.drawable.baseline_info_24)
                .into(kanjiImageView)
        }

        // Configuração do botão de alternância (mostrar/ocultar)
        toggleImageIcon.setOnClickListener {
            if (isImageVisible) {
                kanjiImageView.visibility = View.GONE  // Esconder a imagem
                toggleImageIcon.setImageResource(R.drawable.baseline_disabled_visible_24)  // Alterar ícone para "esconder"
            } else {
                kanjiImageView.visibility = View.VISIBLE  // Mostrar a imagem
                toggleImageIcon.setImageResource(R.drawable.baseline_remove_red_eye_24)  // Alterar ícone para "mostrar"
            }
            isImageVisible = !isImageVisible  // Alternar o estado de visibilidade
        }
    }
}
