package com.example.app_kanji.Desenhar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

// Defina uma interface para a comunicação
interface DrawingCompleteListener {
    fun onDrawingComplete() // Método a ser chamado quando o desenho for concluído
}

class AnimDrawingClass @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK // Cor do pincel
        style = Paint.Style.STROKE
        strokeWidth = 12f // Ajuste a largura do traço, se necessário
        isAntiAlias = true
    }

    private val path = Path() // Armazena o caminho desenhado pelo usuário
    private var drawingCompleteListener: DrawingCompleteListener? = null // Referência para o listener

    init {
        setBackgroundColor(Color.TRANSPARENT) // Torna o fundo transparente
    }

    fun setDrawingCompleteListener(listener: DrawingCompleteListener) {
        drawingCompleteListener = listener // Define o listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint) // Desenha o caminho atual
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y) // Move o caminho para a posição onde o usuário clicou
                invalidate() // Redesenha a view
                return true // Indica que o evento foi tratado
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y) // Adiciona uma linha ao caminho
                invalidate() // Redesenha a view
            }
            MotionEvent.ACTION_UP -> {
                drawingCompleteListener?.onDrawingComplete() // Chama o método do listener ao soltar o toque
            }
        }
        return true // Indica que o evento foi tratado
    }

    fun getPath(): Path {
        return path // Retorna o caminho desenhado pelo usuário
    }

    fun reset() {
        path.reset() // Limpa o caminho
        invalidate() // Redesenha a view
    }
}
