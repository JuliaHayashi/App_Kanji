package com.example.app_kanji.Desenhar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }
    private val path = Path()

    init {
        setBackgroundColor(Color.TRANSPARENT) // Torna o fundo transparente
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
                path.moveTo(x, y) // Move o caminho para a posição inicial
                invalidate() // Redesenha a view
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y) // Adiciona uma linha ao caminho
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Aqui você pode adicionar a lógica de validação se necessário
                // Para continuar, pode ser interessante chamar um callback ou método
            }
        }
        return true
    }

    fun reset() {
        path.reset() // Limpa o caminho
        invalidate() // Redesenha a view
    }
}
