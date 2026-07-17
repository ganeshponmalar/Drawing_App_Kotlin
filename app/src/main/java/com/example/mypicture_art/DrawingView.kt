package com.example.mypicture_art


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class DrawingView (context: Context, attrs: AttributeSet): View(context, attrs){

    //drawing path
    private lateinit var drawPath: FingerPath

    //defines what to draw
    private lateinit var canvasPaint: Paint

    //defines how to draw
    private lateinit var drawPaint: Paint
    private var color = Color.WHITE
    private lateinit var drawCanvas: Canvas
    private lateinit var canvasBitmap: Bitmap
    private var brushSize: Float = 20f

    private val paths = mutableListOf<FingerPath>()


    init{
        setUpDrawing()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x ?: 0f
        val touchY = event?.y ?: 0f

        when (event?.action) {
            //this event will be fired when the user put finger on the screen
            MotionEvent.ACTION_DOWN -> {
                drawPath.color = color
                drawPath.brushThickness = brushSize

                //resetting the path before we see initial point
                drawPath.reset()
                drawPath.moveTo(touchX, touchY)
            }

            //this event will be fired when the user move finger on the screen
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX, touchY)

            }

            //this event will be fired when the user lift finger off the screen
            MotionEvent.ACTION_UP -> {
                drawPaint.color = drawPath.color
                drawPaint.strokeWidth = drawPath.brushThickness
                drawCanvas.drawPath(drawPath, drawPaint)
                paths.add(drawPath)
                drawPath.reset()
            }
            else -> return false
        }
        invalidate()
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)

        for (path in paths){
            drawPaint.color = path.color
            drawPaint.strokeWidth = path.brushThickness
            canvas.drawPath(path, drawPaint)
        }
        
        if (!drawPath.isEmpty) {
            drawPaint.strokeWidth = drawPath.brushThickness
            drawPaint.color = drawPath.color
            canvas.drawPath(drawPath, drawPaint)
        }
    }

    private fun setUpDrawing(){
        brushSize = 20f
        drawPath = FingerPath(color, brushSize)
        drawPaint = Paint()
        drawPaint.color = color
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
        drawPaint.strokeWidth = brushSize
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    internal class FingerPath(var color: Int, var brushThickness: Float) : Path()
}
