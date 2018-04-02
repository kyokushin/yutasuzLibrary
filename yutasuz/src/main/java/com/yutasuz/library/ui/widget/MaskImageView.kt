package com.yutasuz.library.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.yutasuz.library.R

class MaskImageView : AppCompatImageView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        context ?: return
        attrs ?: return
        applyRatio(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context ?: return
        attrs ?: return
        applyRatio(context, attrs, defStyleAttr)
    }

    var widthRatio = -1
    var heightRatio = -1

    // Viewの状態が変更されたときのフラグ
    private var changeSize = false
    private var changeImage = false
    private var changeBackground = false

    private lateinit var drawingCanvas: Canvas
    private var bitmap: Bitmap? = null
    private lateinit var drawingPaint: Paint

    private lateinit var paint: Paint

    private val srcRect = Rect()
    private val dstRect = Rect()
    private val boundRect = RectF()

    private fun applyRatio(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.MaskImageView, defStyleAttr, 0)

        widthRatio = a.getInteger(R.styleable.MaskImageView_widthRatio, -1)
        heightRatio = a.getInteger(R.styleable.MaskImageView_heightRatio, -1)

        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        changeSize = true

        if (widthRatio <= 0 || heightRatio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val origWidth = MeasureSpec.getSize(widthMeasureSpec)
        val origHeight = MeasureSpec.getSize(heightMeasureSpec)

        val aspectRatio = heightRatio / widthRatio.toFloat()
        if (origWidth == 0) {
            val aspectWidth = (height / aspectRatio).toInt()
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(aspectWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        } else if (origHeight == 0) {
            val aspectHeight = (width * aspectRatio).toInt()
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(aspectHeight, MeasureSpec.EXACTLY))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

    }

    private fun setCanvasSize(width: Int, height: Int) {

        bitmap?.recycle()

        bitmap = Bitmap.createBitmap(width - paddingStart - paddingEnd,
                height - paddingTop - paddingBottom, Bitmap.Config.ARGB_8888)
        drawingCanvas = Canvas(bitmap)
        drawingPaint = Paint()
        drawingPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        srcRect.set(0,0, bitmap!!.width, bitmap!!.height)
        boundRect.set(srcRect)
        paint = Paint()
    }

    override fun onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach()
        release()
    }

    private fun release() {
        bitmap?.recycle()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        changeImage = true
        super.setImageBitmap(bm)
    }

    override fun setBackground(background: Drawable?) {
        changeBackground = true
        super.setBackground(background)
    }

    var notInitialized = true

    override fun onDraw(canvas: Canvas?) {

        if (changeSize || changeImage || changeBackground || notInitialized){
            setCanvasSize(width, height)
            generateDrawing()
            dstRect.set(0, 0, width, height)
            changeSize = false
            changeImage = false
            changeBackground = false
        }

        if(bitmap != null) {
            canvas?.drawBitmap(bitmap, srcRect, dstRect, paint)
        }
    }

    @SuppressLint("WrongCall")
    private fun generateDrawing() {
        if(drawable == null){
            notInitialized = true
            return
        }

        drawingCanvas.drawARGB(0, 0, 0, 0)
        background.draw(drawingCanvas)
        val count = drawingCanvas.saveLayer(boundRect, drawingPaint)
        drawingCanvas.concat(imageMatrix)
        drawable.draw(drawingCanvas)
        drawingCanvas.restoreToCount(count)

        notInitialized = false
    }

}