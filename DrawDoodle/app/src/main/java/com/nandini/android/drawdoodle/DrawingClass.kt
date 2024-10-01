package com.nandini.android.drawdoodle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.util.ArrayList

class DrawingClass(context:Context,attrs: AttributeSet):View(context,attrs){

    private var mDrawPath : CustomPath?=null
    private var mCanvasBitmap:Bitmap?=null
    private var mDrawPaint : Paint?=null
    private var mCanvasPaint:Paint?=null
    private var mBrushSize :Float=20.toFloat()
    private var color= Color.BLACK
    private var canvas : Canvas?=null
    private var mPaths=ArrayList<CustomPath>()
    private var mUndoPaths=ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    fun onUndo(){
        if(mPaths.size>0){
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }

    private fun setUpDrawing() {
        mDrawPaint= Paint()
        mDrawPath=CustomPath(color,mBrushSize)
        mDrawPaint!!.color=color
        mDrawPaint!!.style=Paint.Style.STROKE
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint=Paint(Paint.DITHER_FLAG)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas=Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(paths in mPaths){
            mDrawPaint!!.strokeWidth=paths.brushThickness
            mDrawPaint!!.color=paths.color
            canvas.drawPath(paths, mDrawPaint!!)
        }
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
        if(!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness
            mDrawPaint!!.color=mDrawPaint!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
} }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var touchX=event?.x
        var touchY=event?.y

        when(event?.action){

            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color=color
                mDrawPath!!.brushThickness=mBrushSize
                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!,touchY!!)
            }

            MotionEvent.ACTION_MOVE ->{
                mDrawPath!!.lineTo(touchX!!,touchY!!)
            }

            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!)
                mDrawPath=CustomPath(color,mBrushSize)
            }
            else-> return false
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newSize:Float){
        mBrushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)
        mDrawPaint!!.strokeWidth=mBrushSize
    }

    fun setColor(newColor:String){
        color=Color.parseColor(newColor)
    }

    // internal -> class will be available for this module only.
    // inner -> nested class(class inside a class) -> which can access the member variable & fns of the outer class(drawingView class.)
    internal inner class CustomPath(var color:Int,var brushThickness:Float): Path() {

    }


}