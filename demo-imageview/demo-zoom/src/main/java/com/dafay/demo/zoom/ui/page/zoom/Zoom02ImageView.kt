package com.dafay.demo.zoom.ui.page.zoom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.zoom.utils.scaleX
import com.dafay.demo.zoom.utils.translateBy
import com.dafay.demo.zoom.utils.zoomBy
import com.dafay.demo.zoom.utils.zoomTo

/**
 * 实现功能
 * 1. 支持双指缩放
 * 问题：
 * 1. scaleGestureDetector、gestureDetector 响应
 * 2. 可添加 onScroll、onScale 的触发 scaledTouchSlop 限定
 */
class Zoom02ImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // 手势检测器
    private val gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector
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
                // 上面返回 false,scaleGestureDetector.onTouchEvent(event) 便会返回 true,onDoubleTap 事件得以响应，这块逻辑待深入研究
                return scaleGestureDetector.onTouchEvent(event)
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