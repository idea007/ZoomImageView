package com.dafay.demo.zoom.ui.page.gesture

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
 * 边界处理，左上右下移动超出边界时进行矫正
 */
class Zoom04ImageView @kotlin.jvm.JvmOverloads constructor(
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
        playZoomAnim(e.x, e.y)
    }

    private fun playZoomAnim(pivotX: Float, pivotY: Float) {
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
     * 在显示之前，进行边界矫正，对 suppMatrix 进行调整
     */
    private fun correctSuppMatrix() {
        // 目标 matrix
        val tempMatrix = Matrix(originMatrix).apply { postConcat(suppMatrix) }
        // 得到 matrix 的 rect
        val tempRectF = getDrawMatrixRect(tempMatrix)
        tempRectF ?: return
        var deltaX = 0f
        var deltaY = 0f

        if (tempRectF.height() < height) {
            deltaY = ((height - tempRectF.height()) / 2) - tempRectF.top
        } else if (tempRectF.top > 0) {
            deltaY = -tempRectF.top
        } else if (tempRectF.bottom < height) {
            deltaY = height - tempRectF.bottom
        }

        if (tempRectF.width() <= width) {
            deltaX = ((width - tempRectF.width()) / 2) - tempRectF.left
        } else if (tempRectF.left > 0) {
            deltaX = -tempRectF.left
        } else if (tempRectF.right < width) {
            deltaX = width - tempRectF.right
        }
        suppMatrix.translateBy(deltaX, deltaY)
    }

    private fun getDrawMatrixRect(matrix: Matrix): RectF? {
        val d = drawable
        if (null != d) {
            val tempRect = RectF()
            // 什么新奇的写法
            tempRect[0f, 0f, d.intrinsicWidth.toFloat()] = d.intrinsicHeight.toFloat()
            matrix.mapRect(tempRect)
            return tempRect
        }
        return null
    }

    /**
     * 应用于 ImageView 的 matrix，为了思路清晰，这里先频繁创建对象 drawMatrix
     */
    private fun applyToImageMatrix() {
        // 在应用之前，进行边界矫正
        correctSuppMatrix()
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