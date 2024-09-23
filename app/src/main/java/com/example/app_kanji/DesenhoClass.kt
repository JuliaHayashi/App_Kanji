package com.example.app_kanji

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DesenhoClass(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val mainPaint: Paint
    private var pathList = ArrayList<Path>()

    init {
        mainPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
    }

    private var xPos = 0f
    private var yPos = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE) // Limpa o fundo
        for (path in pathList) {
            canvas.drawPath(path, mainPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    xPos = it.x
                    yPos = it.y
                    invalidate()

                    val path = Path()
                    path.moveTo(xPos, yPos)
                    pathList.add(path)
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    xPos = it.x
                    yPos = it.y
                    pathList.lastOrNull()?.lineTo(xPos, yPos)
                    invalidate()
                    return true
                }

                else -> {}
            }
        }
        return false
    }

    fun clear() {
        pathList.clear() // Limpa a lista de caminhos
        invalidate() // Requere que a view seja redesenhada
    }
}
