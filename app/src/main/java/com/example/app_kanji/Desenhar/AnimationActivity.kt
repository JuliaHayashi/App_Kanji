package com.example.app_kanji.Desenhar

import android.animation.ObjectAnimator
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.PathParser
import com.example.app_kanji.R
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class AnimationActivity : AppCompatActivity() {

    private lateinit var svgImageView: ImageView
    private lateinit var backButton: ImageView
    private val handler = Handler()
    private val strokeDelay: Long = 500 // Tempo de atraso entre cada traço (em milissegundos)
    private var currentStrokeIndex = 0
    private lateinit var paths: List<Path>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)

        svgImageView = findViewById(R.id.svgImageView)
        backButton = findViewById(R.id.backButton)

        // Carrega o SVG a partir do recurso raw
        loadSvgFromResource(R.raw.u8a71)

        // Configura o botão de voltar
        backButton.setOnClickListener {
            finish()
        }
    }

    // Função para carregar o SVG como XML a partir de um recurso
    private fun loadSvgFromResource(resourceId: Int) {
        try {
            // Obtém o SVG a partir do recurso raw como um InputStream
            val inputStream: InputStream = resources.openRawResource(resourceId)

            // Converte o SVG em um Document XML
            val svgDocument = parseSvgToDocument(inputStream)

            // Extrai os caminhos do SVG
            paths = extractPathsFromSVG(svgDocument)

            // Inicia a animação dos traços
            animateStrokes()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Função para converter o SVG em um Document XML
    private fun parseSvgToDocument(inputStream: InputStream): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputSource = InputSource(inputStream)
        return builder.parse(inputSource)
    }

    // Extrai os caminhos do SVG
    private fun extractPathsFromSVG(svgDocument: Document): List<Path> {
        val pathList = mutableListOf<Path>()
        val pathElements = svgDocument.getElementsByTagName("path")

        // Itera sobre os elementos path no SVG
        for (i in 0 until pathElements.length) {
            val pathElement = pathElements.item(i)
            val pathData = pathElement.attributes.getNamedItem("d").nodeValue // Obtém o atributo 'd'
            val path = PathParser.createPathFromPathData(pathData) // Converte para Path
            pathList.add(path)
        }

        return pathList
    }

    // Função para animar os traços do SVG
    private fun animateStrokes() {
        currentStrokeIndex = 0
        drawNextStroke()
    }

    private fun drawNextStroke() {
        // Verifica se ainda há traços para desenhar
        if (currentStrokeIndex < paths.size) {
            // Anima o traço atual
            animatePath(currentStrokeIndex)

            // Aumenta o índice do traço atual
            currentStrokeIndex++

            // Chama a próxima animação após um atraso
            handler.postDelayed({ drawNextStroke() }, strokeDelay)
        }
    }

    private fun animatePath(index: Int) {
        // Cria um Canvas e desenha o caminho atual
        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // Cor de fundo

        // Define a pintura para o traço
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f // Largura do traço
            color = Color.BLACK
        }

        // Desenha o caminho atual
        canvas.drawPath(paths[index], paint)

        // Cria um PictureDrawable a partir do bitmap
        val animatedDrawable = BitmapDrawable(resources, bitmap)

        // Define o drawable animado no ImageView
        svgImageView.setImageDrawable(animatedDrawable)

        // Anima a opacidade do ImageView
        val animator = ObjectAnimator.ofFloat(svgImageView, "alpha", 0f, 1f)
        animator.duration = 300 // Duração da animação para cada traço
        animator.start()
    }
}
