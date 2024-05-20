package com.dafay.demo.zoom.ui.page.zoom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.utils.scaleX
import com.dafay.demo.zoom.utils.translateBy
import com.dafay.demo.zoom.utils.zoomBy
import com.dafay.demo.zoom.utils.zoomTo

/**
 * 实现功能
 * 1. 初始化处理
 * 问题：
 * 1. 同一张图片的高清、低清切换的问题（图片宽高比一样）,例如执行放大过程中切换
 */
class Zoom03ImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // 手势检测器
    private val gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector

    // 默认类似 fitCenter 显示模式时的矩阵
    private val originMatrix = Matrix()
    private val suppMatrix = Matrix()
    private var minZoom = DEFAULT_MIN_ZOOM
    private var maxZoom = DEFAULT_MAX_ZOOM
    private var zoomAnim = ValueAnimator().apply { duration = DEFAULT_ANIM_DURATION }
    private val pivotPointF = PointF(0f, 0f)

    init {
        scaleType = ScaleType.MATRIX
        val multiGestureDetector = MultiGestureDetector()
        gestureDetector = GestureDetector(context, multiGestureDetector)
        scaleGestureDetector = ScaleGestureDetector(context, multiGestureDetector)
        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (gestureDetector.onTouchEvent(event)) {
                    return true
                }
                return scaleGestureDetector.onTouchEvent(event)
            }
        })
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        updateOriginMatrix(drawable)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        updateOriginMatrix(drawable)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        updateOriginMatrix(drawable)
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        updateOriginMatrix(drawable)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateOriginMatrix(drawable)
    }

    /**
     * 计算图片显示类似 fitCenter 效果时的矩阵（忽视 pading，只处理 fitCenter 这一种显示模式）
     */
    private fun updateOriginMatrix(drawable: Drawable?) {
        drawable ?: return
        if (width <= 0) {
            return
        }
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        originMatrix.reset()
        val tempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
        val tempDst = RectF(0f, 0f, viewWidth, viewHeight)
        originMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER)
        applyToImageMatrix()
    }

    /**
     * 处理双击事件，双击执行缩放动画
     */
    private fun dealOnDoubleTap(e: MotionEvent) {
        playZoomAnim(e.x,e.y)
    }

    fun playZoomAnim(pivotX:Float,pivotY:Float){
        zoomAnim.removeAllUpdateListeners()
        zoomAnim.cancel()
        // 点击的点设置为缩放的中心点
        pivotPointF.set(pivotX, pivotY)
        val animatorUpdateListener = object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val tempValue = animation.animatedValue as Float
                suppMatrix.zoomTo(tempValue, pivotPointF.x, pivotPointF.y)
                applyToImageMatrix()
            }
        }
        val currZoom = suppMatrix.scaleX()
        val endZoom = if (Math.abs(currZoom - maxZoom) > Math.abs(currZoom - minZoom)) maxZoom else minZoom
        zoomAnim.setFloatValues(currZoom, endZoom)
        zoomAnim.addUpdateListener(animatorUpdateListener)
        zoomAnim.start()
    }

    /**
     * 处理拖动（平移）事件
     */
    private fun dealOnScroll(distanceX: Float, distanceY: Float) {
        suppMatrix.translateBy(-distanceX, -distanceY)
        applyToImageMatrix()
    }

    /**
     * 处理双指缩放事件
     */
    private fun dealOnScale(detector: ScaleGestureDetector) {
        val currScale: Float = suppMatrix.scaleX()
        var scaleFactor = detector.scaleFactor
        if ((currScale >= maxZoom && scaleFactor > 1f) || (currScale <= minZoom && scaleFactor < 1f)) {
            return
        }
        suppMatrix.zoomBy(scaleFactor, detector.focusX, detector.focusY)
        applyToImageMatrix()
    }

    /**
     * 应用于 ImageView 的 matrix，为了思路清晰，这里先频繁创建对象 drawMatrix
     */
    fun applyToImageMatrix() {
        val drawMatrix = Matrix()
        drawMatrix.set(originMatrix)
        // drawMatrix = suppMatrix * originMatrix
        drawMatrix.postConcat(suppMatrix)
        debug("originMatrix:${originMatrix} suppMatrix:${suppMatrix} drawMatrix:${drawMatrix}")
        imageMatrix = drawMatrix
    }

    inner class MultiGestureDetector : GestureDetector.SimpleOnGestureListener(),
        ScaleGestureDetector.OnScaleGestureListener {

        override fun onDoubleTap(e: MotionEvent): Boolean {
            dealOnDoubleTap(e)
            return super.onDoubleTap(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            dealOnScroll(distanceX, distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            dealOnScale(detector)
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
        }
    }
}