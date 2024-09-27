package com.example.app_kanji.Desenhar

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
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
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var paint: Paint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)

        svgImageView = findViewById(R.id.svgImageView)
        backButton = findViewById(R.id.backButton)

        // Inicializa o bitmap e o canvas
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // Cor de fundo

        // Inicializa a pintura
        paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f // Largura do traço
            color = Color.BLACK
        }

        // Carrega o SVG a partir do recurso raw
        loadSvgFromResource(R.raw.u59b9)

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
        if (currentStrokeIndex < paths.size) {
            animatePath(currentStrokeIndex) {
                val drawingView: DrawingView = findViewById(R.id.drawingView)
                drawingView.reset() // Limpar qualquer desenho anterior
                drawingView.visibility = View.VISIBLE // Tornar a view visível para o desenho

                drawingView.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        // Aqui você pode validar o desenho do usuário
                        // Se correto, chamar a próxima animação
                        currentStrokeIndex++ // Aumentar o índice do traço atual
                        drawNextStroke() // Chamar o próximo traço
                        drawingView.visibility = View.GONE // Esconder a DrawingView
                    }
                    true
                }
            }
        } else {
            // Esconder a DrawingView após todos os traços serem desenhados
            val drawingView: DrawingView = findViewById(R.id.drawingView)
            drawingView.visibility = View.GONE
        }
    }


    private fun animatePath(index: Int, onComplete: () -> Unit) {
        val path = paths[index]
        val pathMeasure = PathMeasure(path, false)

        // Cria um ValueAnimator para animar o traço
        val animator = ValueAnimator.ofFloat(0f, pathMeasure.length)
        animator.duration = 500 // Duração da animação para cada traço
        animator.addUpdateListener { animation ->
            val length = animation.animatedValue as Float

            // Limpa o canvas apenas na primeira animação
            if (currentStrokeIndex == 0 && index == 0) {
                canvas.drawColor(Color.WHITE) // Limpa o canvas apenas na primeira animação
            }

            // Cria um novo path que representará o traço animado
            val animatedPath = Path()
            pathMeasure.getSegment(0f, length, animatedPath, true)

            // Desenha o path animado no canvas
            canvas.drawPath(animatedPath, paint)

            // Cria um PictureDrawable a partir do bitmap atualizado
            val animatedDrawable = BitmapDrawable(resources, bitmap)

            // Define o drawable animado no ImageView
            svgImageView.setImageDrawable(animatedDrawable)
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Lógica opcional para quando a animação começa
            }

            override fun onAnimationEnd(animation: Animator) {
                onComplete() // Chama a função de conclusão ao final da animação
            }

            override fun onAnimationCancel(animation: Animator) {
                // Lógica opcional para quando a animação é cancelada
            }

            override fun onAnimationRepeat(animation: Animator) {
                // Lógica opcional para quando a animação é repetida
            }
        })

        animator.start()
    }
}
