package com.example.mypicture_art


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

/**
 * A custom View class that provides a canvas for drawing.
 */
class DrawingView (context: Context, attrs: AttributeSet): View(context, attrs){

    // Variable for the path being drawn currently
    private var drawPath: FingerPath? = null

    // Paint object that defines how the canvas is drawn
    private lateinit var canvasPaint: Paint

    // Paint object that defines how the path is drawn (color, thickness, etc.)
    private lateinit var drawPaint: Paint
    
    // Default color for drawing
    private var color = Color.WHITE
    
    // Canvas where the drawing is actually performed and stored
    private lateinit var drawCanvas: Canvas
    
    // Bitmap that holds the drawing pixels
    private lateinit var canvasBitmap: Bitmap
    
    // Default brush size
    private var brushSize: Float = 20f

    // A list to store all the paths drawn by the user to keep them on screen
    private val paths = mutableListOf<FingerPath>() 


    init{
        setUpDrawing()
    }

    /**
     * Called when the view size changes (e.g., on initialization).
     * We initialize the bitmap and canvas here based on the view dimensions.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
    }

    /**
     * Handles touch events to enable drawing.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x ?: 0f
        val touchY = event?.y ?: 0f

        when (event?.action) {
            // Finger touches the screen: start a new path
            MotionEvent.ACTION_DOWN -> {
                drawPath = FingerPath(color, brushSize)
                drawPath?.moveTo(touchX, touchY)
            }

            // Finger moves: draw lines connecting the points
            MotionEvent.ACTION_MOVE -> {
                drawPath?.lineTo(touchX, touchY)
            }

            // Finger lifted: finish the path and save it to the canvas and list
            MotionEvent.ACTION_UP -> {
                drawPath?.let {
                    drawPaint.color = it.color
                    drawPaint.strokeWidth = it.brushThickness
                    drawCanvas.drawPath(it, drawPaint)
                    paths.add(it)
                }
                drawPath = null
            }
            else -> return false
        }
        
        // Redraw the view
        invalidate()
        
        // Required for accessibility when overriding onTouchEvent
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    /**
     * Draws the content of the view.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw the background bitmap that stores previous strokes
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)

        // Draw all completed paths from the list
        for (path in paths){
            drawPaint.color = path.color
            drawPaint.strokeWidth = path.brushThickness
            canvas.drawPath(path, drawPaint)
        }
        
        // Draw the path currently being drawn by the user
        drawPath?.let {
            drawPaint.strokeWidth = it.brushThickness
            drawPaint.color = it.color
            canvas.drawPath(it, drawPaint)
        }
    }

    /**
     * Initializes the drawing properties (Paint objects).
     */
    private fun setUpDrawing(){
        drawPaint = Paint()
        drawPaint.color = color
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    /**
     * Updates the brush size and converts DIP to pixels.
     */
    fun changeBurshSize(newSize: Float){
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,resources.displayMetrics
        )
        drawPaint.strokeWidth = brushSize
    }

    /**
     * Sets the color for drawing.
     */
    fun setColor(newColor: Int) {
        color = newColor
        drawPaint.color = color
    }

    /**
     * Logic to undo the last stroke.
     */
    fun onClickUndo() {
        if (paths.size > 0) {
            paths.removeAt(paths.size - 1)
            // Re-draw the canvas bitmap without the removed path
            drawCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
            for (path in paths) {
                drawPaint.color = path.color
                drawPaint.strokeWidth = path.brushThickness
                drawCanvas.drawPath(path, drawPaint)
            }
            invalidate()
        }
    }

    /**
     * Custom Path class that stores its own color and thickness.
     */
    internal class FingerPath(var color: Int, var brushThickness: Float) : Path()
}
