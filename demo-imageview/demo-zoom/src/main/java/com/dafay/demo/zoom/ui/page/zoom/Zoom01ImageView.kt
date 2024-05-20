package com.dafay.demo.zoom.ui.page.zoom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.zoom.utils.scaleX
import com.dafay.demo.zoom.utils.translateBy
import com.dafay.demo.zoom.utils.zoomTo

/**
 * 实现功能
 * 1. scaleType = MATRIX
 * 1. 图片双击放大或缩小，点击点为缩放中心点（支点）
 * 2. 拖动
 */
class Zoom01ImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val gestureDetector: GestureDetector
    private val originMatrix = Matrix()
    private val suppMatrix = Matrix()
    private var minZoom = DEFAULT_MIN_ZOOM
    private var maxZoom = DEFAULT_MAX_ZOOM
    private var zoomAnim = ValueAnimator().apply { duration = DEFAULT_ANIM_DURATION }
    private val pivotPointF = PointF(0f, 0f)

    init {
        scaleType = ScaleType.MATRIX
        gestureDetector = GestureDetector(context, MySimpleOnGestureListener())
        setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })
    }

    /**
     * 处理双击事件，双击执行缩放动画
     */
    private fun dealOnDoubleTap(e: MotionEvent) {
        zoomAnim.removeAllUpdateListeners()
        zoomAnim.cancel()
        // 点击的点设置为缩放的中心点
        pivotPointF.set(e.x, e.y)
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
     * 应用于 ImageView 的 matrix，为了思路清晰，这里先频繁创建对象 drawMatrix
     */
    fun applyToImageMatrix() {
        val drawMatrix = Matrix()
        drawMatrix.set(originMatrix)
        // drawMatrix = suppMatrix * originMatrix
        drawMatrix.postConcat(suppMatrix)
        imageMatrix = drawMatrix
    }

    inner class MySimpleOnGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            // 返回 true，GestureDetector 一系列手势才能响应
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            dealOnDoubleTap(e)
            return super.onDoubleTap(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            dealOnScroll(distanceX, distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }
}