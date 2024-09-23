package com.example.app_kanji

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DesenhoClass(context: Context?, attrs: AttributeSet?) : View(context, attrs){

    val mainPaint: Paint

    init {
        mainPaint = Paint()
        mainPaint.color = Color.BLACK
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = 10f
    }

    var xPos = 0f
    var yPos = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        //canvas.drawCircle(xPos,yPos,100f,mainPaint)
        if(!pathList.isEmpty()){
            for(path in pathList){
                canvas.drawPath(path,mainPaint)
            }
        }


    }


    var pathList = ArrayList<Path>()
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event!!.action == MotionEvent.ACTION_DOWN){
            xPos = event!!.x
            yPos = event.y
            invalidate()

            val path = Path()
            path.moveTo(xPos,yPos)
            pathList.add(path)

            return true
        }else if(event.action ==MotionEvent.ACTION_MOVE){
            xPos = event!!.x
            yPos = event.y
            invalidate()

            pathList.get(pathList.size -1).lineTo(xPos,yPos)


            return true
        }
        else{
            return false
        }

    }
}