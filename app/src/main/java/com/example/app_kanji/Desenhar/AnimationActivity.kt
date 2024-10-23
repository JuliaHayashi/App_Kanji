package com.example.app_kanji.Desenhar

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
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
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var pathPaint: Paint
    private var currentStrokeIndex = 0
    private lateinit var paths: List<Path>

    private var svgWidth = 200f // Ajuste conforme necessário
    private var svgHeight = 200f // Ajuste conforme necessário

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)

        svgImageView = findViewById(R.id.svgImageView)
        backButton = findViewById(R.id.backButton)

        // Inicializa o bitmap e o canvas
        bitmap = Bitmap.createBitmap(svgWidth.toInt(), svgHeight.toInt(), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // Cor de fundo

        // Inicializa a pintura do caminho da animação
        pathPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 8f // Largura do traço
            color = Color.LTGRAY // Cor da animação
        }

        // Recebe o ID do recurso SVG do Intent
        val resourceId = intent.getIntExtra("KANJI_SVG_RESOURCE_ID", 0)
        if (resourceId != 0) {
            loadSvgFromResource(resourceId)
        } else {
            Toast.makeText(this, "Erro ao carregar o kanji", Toast.LENGTH_SHORT).show()
        }

        // Configura o botão de voltar
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadSvgFromResource(resourceId: Int) {
        try {
            val inputStream: InputStream = resources.openRawResource(resourceId)
            val svgDocument = parseSvgToDocument(inputStream)
            paths = extractPathsFromSVG(svgDocument)
            animateStrokes() // Chama a animação para desenhar todos os traços juntos
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseSvgToDocument(inputStream: InputStream): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputSource = InputSource(inputStream)
        return builder.parse(inputSource)
    }

    private fun extractPathsFromSVG(svgDocument: Document): List<Path> {
        val pathList = mutableListOf<Path>()
        val pathElements = svgDocument.getElementsByTagName("path")
        for (i in 0 until pathElements.length) {
            val pathElement = pathElements.item(i)
            val pathData = pathElement.attributes.getNamedItem("d").nodeValue
            val path = PathParser.createPathFromPathData(pathData)
            pathList.add(path)
        }
        return pathList
    }

    private fun animateStrokes() {
        currentStrokeIndex = 0 // Certifique-se de iniciar a partir do primeiro traço
        drawNextStroke() // Inicia a animação dos traços
    }

    private fun drawNextStroke() {
        if (currentStrokeIndex < paths.size) {
            animatePath(currentStrokeIndex) {
                currentStrokeIndex++ // Move para o próximo traço
                drawNextStroke() // Desenha o próximo traço
            }
        }
    }

    private fun animatePath(index: Int, onComplete: () -> Unit) {
        val path = paths[index]
        val pathMeasure = PathMeasure(path, false)

        // Cria um ValueAnimator para animar o traço
        val animator = ValueAnimator.ofFloat(0f, pathMeasure.length)
        animator.duration = 1000 // Duração da animação para cada traço
        animator.addUpdateListener { animation ->
            val length = animation.animatedValue as Float
            // Limpa o canvas apenas no primeiro traço, depois mantém
            if (currentStrokeIndex == 0) {
                canvas.drawColor(Color.WHITE) // Limpa o canvas apenas na primeira animação
            }

            // Anima o traço atual
            val animatedPath = Path()
            pathMeasure.getSegment(0f, length, animatedPath, true)
            canvas.drawPath(animatedPath, pathPaint) // Desenha o caminho da animação atual

            // Atualiza a imagem do SVG
            val animatedDrawable = BitmapDrawable(resources, bitmap)
            svgImageView.setImageDrawable(animatedDrawable)
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                onComplete() // Chama o callback quando a animação termina
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator.start()
    }
}
