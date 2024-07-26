package com.example.app_kanji

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class Desenhar(context: Context?, attrrs: AttributeSet?):View(context,attrrs)
{
    
    val mainPaint: Paint
    init {
        mainPaint = Paint()
        mainPaint.color = Color.RED
    }

    var xPos = 0f
    var yPos = 0f

    override fun onDraw(canvas: Canvas?){
        canvas!!.drawCircle(xPos, yPos,100f,mainPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean{
        xPos = event!!.x
        yPos = event.y
        invalidate()
        return super.onTouchEvent(event)
    }

}