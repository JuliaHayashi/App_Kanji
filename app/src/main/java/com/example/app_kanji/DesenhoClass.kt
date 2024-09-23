package com.example.app_kanji

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.DashPathEffect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DesenhoClass(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val mainPaint: Paint
    private val linePaint: Paint
    private val darkLinePaint: Paint
    private var pathList = ArrayList<Path>()
    private var lastX = 0f
    private var lastY = 0f

    init {
        mainPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 18f
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        linePaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 4f
            pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        }

        darkLinePaint = Paint().apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        drawDashedLines(canvas)
        drawDarkLines(canvas)

        for (path in pathList) {
            canvas.drawPath(path, mainPaint)
        }
    }

    private fun drawDashedLines(canvas: Canvas) {
        val spacing = height / 4f

        for (i in 1..3) {
            val y = i * spacing
            canvas.drawLine(0f, y, width.toFloat(), y, linePaint)
        }

        val verticalSpacing = width / 4f
        for (i in 1..3) {
            val x = i * verticalSpacing
            canvas.drawLine(x, 0f, x, height.toFloat(), linePaint)
        }
    }

    private fun drawDarkLines(canvas: Canvas) {
        val spacing = height / 4f
        canvas.drawLine(0f, spacing * 2, width.toFloat(), spacing * 2, darkLinePaint)
        val verticalSpacing = width / 4f
        canvas.drawLine(verticalSpacing * 2, 0f, verticalSpacing * 2, height.toFloat(), darkLinePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = it.x
                    lastY = it.y
                    val path = Path()
                    path.moveTo(lastX, lastY)
                    pathList.add(path)
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentX = it.x
                    val currentY = it.y
                    pathList.lastOrNull()?.let { path ->
                        drawSmoothCurve(path, lastX, lastY, currentX, currentY)
                    }
                    lastX = currentX
                    lastY = currentY
                    invalidate()
                    return true
                }

                else -> {}
            }
        }
        return false
    }

    private fun drawSmoothCurve(path: Path, startX: Float, startY: Float, endX: Float, endY: Float) {
        val controlX = (startX + endX) / 2
        val controlY = (startY + endY) / 2

        path.quadTo(controlX, controlY, endX, endY)
    }

    fun clear() {
        pathList.clear()
        invalidate()
    }
}
