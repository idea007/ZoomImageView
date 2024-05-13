package com.dafay.demo.zoom.ui.page.gesture

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
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.utils.scaleX
import com.dafay.demo.zoom.utils.toPrint
import com.dafay.demo.zoom.utils.translateBy
import com.dafay.demo.zoom.utils.zoomTo

/**
 * 功能
 * 1. 图片双击放大或缩小，以点击点为缩放中心点
 * 2. 可拖到
 * 问题：多次缩放之后图片大小变了
 * 解决：matrix 扩展函数，zoomTo/zoomBy
 */
class Gesture01ImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // 手势检测器
    private val gestureDetector: GestureDetector
    private val originMatrix = Matrix()
    val suppMatrix = Matrix()

    private var minZoom = com.dafay.demo.zoom.ui.page.gesture.DEFAULT_MIN_ZOOM
    private var maxZoom = com.dafay.demo.zoom.ui.page.gesture.DEFAULT_MAX_ZOOM
    private var zoomAnim = ValueAnimator().apply { duration =
        com.dafay.demo.zoom.ui.page.gesture.DEFAULT_ANIM_DURATION
    }

    init {
        gestureDetector = GestureDetector(context, MySimpleOnGestureListener())
        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
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
            zoomAnim = ValueAnimator.ofFloat(currZoom, maxZoom).apply { duration =
                com.dafay.demo.zoom.ui.page.gesture.DEFAULT_ANIM_DURATION
            }
            zoomAnim.addUpdateListener(animatorUpdateListener)
            zoomAnim.start()
        } else {
            zoomAnim = ValueAnimator.ofFloat(currZoom, minZoom).apply { duration =
                com.dafay.demo.zoom.ui.page.gesture.DEFAULT_ANIM_DURATION
            }
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

    inner class MySimpleOnGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            debug("onDown e=${e.toPrint()}")
            // 返回 true，Gesture 一系列手势才能响应
            return true
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

    }
}