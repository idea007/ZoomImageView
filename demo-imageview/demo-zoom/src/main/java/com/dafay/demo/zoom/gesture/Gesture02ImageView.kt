package com.dafay.demo.zoom.gesture

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.utils.scaleX
import com.dafay.demo.zoom.utils.toPrint
import com.dafay.demo.zoom.utils.translateBy
import com.dafay.demo.zoom.utils.zoomBy
import com.dafay.demo.zoom.utils.zoomTo

/**
 * 功能
 * 1. 支持双指缩放
 * 问题：
 * 1. scaleGestureDetector、gestureDetector 响应
 * 2. 可添加 onScroll、onScale 的触发 scaledTouchSlop 限定
 */
class Gesture02ImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // 手势检测器
    private val gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector
    private val originMatrix = Matrix()
    val suppMatrix = Matrix()

    private var minZoom = DEFAULT_MIN_ZOOM
    private var maxZoom = DEFAULT_MAX_ZOOM
    private var zoomAnim = ValueAnimator().apply { duration = DEFAULT_ANIM_DURATION }

    init {
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

    /**
     * 处理双击事件
     * 双击执行缩放动画
     */
    private fun dealOnDoubleTap(e: MotionEvent) {
        // 点击的点设置为缩放的中心点
        pivotPointF.apply {
            x = e.x
            y = e.y
        }

        val animatorUpdateListener = object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val tempValue = animation.animatedValue as Float
                suppMatrix.zoomTo(tempValue, pivotPointF.x, pivotPointF.y)
                debug("tempValue=${tempValue} currScale=${suppMatrix.scaleX()} currMatrix:${suppMatrix.toPrint()}")
                applyToImageMatrix()
            }
        }
        val currZoom = suppMatrix.scaleX()
        if (Math.abs(currZoom - maxZoom) > Math.abs(currZoom - minZoom)) {
            zoomAnim = ValueAnimator.ofFloat(currZoom, maxZoom).apply { duration = DEFAULT_ANIM_DURATION }
            zoomAnim.addUpdateListener(animatorUpdateListener)
            zoomAnim.start()
        } else {
            zoomAnim = ValueAnimator.ofFloat(currZoom, minZoom).apply { duration = DEFAULT_ANIM_DURATION }
            zoomAnim.addUpdateListener(animatorUpdateListener)
            zoomAnim.start()
        }
    }

    /**
     * 处理拖动（平移）事件
     */
    private fun dealOnScroll(distanceX: Float, distanceY: Float) {
        suppMatrix.translateBy(-distanceX, -distanceY)
        applyToImageMatrix()
    }

    /**
     * 处理双指缩放
     */
    private fun dealOnScale(detector: ScaleGestureDetector) {
        val currScale: Float = suppMatrix.scaleX()
        var scaleFactor = detector.scaleFactor
        debug("currScale=${currScale} scaleFactor=${scaleFactor} detector.focusX=${detector.focusX} detector.focusY=${detector.focusY}")
        if (currScale < minZoom || currScale > maxZoom) {
            return
        }
        // 临界值限制
        if (currScale * scaleFactor < minZoom) {
            scaleFactor = minZoom / currScale
        }
        // 临界值限制
        if (currScale * scaleFactor > maxZoom) {
            scaleFactor = maxZoom / currScale
        }
        suppMatrix.zoomBy(scaleFactor, detector.focusX, detector.focusY)
        applyToImageMatrix()
    }

    /**
     * 应用于 ImageView 的 matrix
     * 为了思路清晰，这里频繁创建对象 drawMatrix
     */
    fun applyToImageMatrix() {
        val drawMatrix = Matrix()
        drawMatrix.set(originMatrix)
        // 即当前 Matrix 会乘以传入的 Matrix。
        drawMatrix.postConcat(suppMatrix)
        imageMatrix = drawMatrix
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        debug("onDraw")
        drawAuxiliary(canvas)
    }

    private val pivotPointF = PointF(0f, 0f)

    /**
     * 画辅助点
     */
    private fun drawAuxiliary(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setStyle(Paint.Style.STROKE)
        paint.strokeWidth = 2f
        paint.setColor(Color.RED)
        canvas.drawCircle(pivotPointF.x, pivotPointF.y, 8f, paint)
    }

    inner class MultiGestureDetector : GestureDetector.SimpleOnGestureListener(),
        ScaleGestureDetector.OnScaleGestureListener {
        override fun onDown(e: MotionEvent): Boolean {
            debug("onDown e=${e.toPrint()}")
            // 返回 true，Gesture 一系列手势才能响应
            return super.onDown(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            debug("onDoubleTapEvent e=${e.toPrint()}")
            dealOnDoubleTap(e)
            return super.onDoubleTap(e)
        }

        /**
         * 当滚动发生时，初始 on down MotionEvent 和当前移动 MotionEvent 发生时发出通知。
         * 为方便起见，还提供了 x 和 y 中的距离
         * @param distanceX 自上次调用 onScroll 以来沿 X 轴滚动的距离（两次回调的差值）。这不是 e1(是 down) 和 e2 之间的距离。
         */
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            debug("onScroll e1=${e1?.toPrint()} e2=${e2.toPrint()} distanceX=${distanceX} distanceY=${distanceY}")
            dealOnScroll(distanceX, distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            dealOnScale(detector)
            debug("onScale detector=${detector.toPrint()}")
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
        }
    }


}