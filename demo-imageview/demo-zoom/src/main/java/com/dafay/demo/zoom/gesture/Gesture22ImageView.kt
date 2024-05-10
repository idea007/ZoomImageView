package com.dafay.demo.zoom.gesture

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.utils.scaleX
import com.dafay.demo.zoom.utils.toPrint
import com.dafay.demo.zoom.utils.transX
import com.dafay.demo.zoom.utils.transY

/**
 * 示例
 * 1. 图片双击缩放
 * 2. 放大效果下能平移
 * 问题：
 * 平移之后缩放动画位置不对
 */
class Gesture22ImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // 手势检测器
    private val gestureDetector: GestureDetector

    private val originMatrix = Matrix()
    private val suppMatrix = Matrix()

    private val simpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            debug("onDown e=${e.toPrint()}")
            return true
            // return super.onDown(e)
        }

        override fun onShowPress(e: MotionEvent) {
            debug("onShowPress e=${e.toPrint()}")
            super.onShowPress(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            debug("onSingleTapConfirmed e=${e.toPrint()}")
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) {
            debug("onLongPress e=${e.toPrint()}")
            super.onLongPress(e)
        }

        override fun onDoubleTapEvent(event: MotionEvent): Boolean {

            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            debug("onDoubleTapEvent e=${e.toPrint()}")
            dealZoomAnim(e)
            return super.onDoubleTap(e)
        }

        /**
         * 当滚动发生时，初始 on down MotionEvent 和当前移动 MotionEvent 发生时发出通知。
         * 为方便起见，还提供了 x 和 y 中的距离
         * @param distanceX 自上次调用 onScroll 以来沿 X 轴滚动的距离。这不是 e1(是 down) 和 e2 之间的距离。
         */
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            debug("onScroll e1=${e1?.toPrint()} e2=${e2.toPrint()} distanceX=${distanceX} distanceY=${distanceY}")
            dealTrans(distanceX, distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        /**
         * 当发生 fling 事件时，使用初始 on down MotionEvent 和匹配的 up MotionEvent 通知该事件。
         * 计算出的速度沿 x 轴和 y 轴提供，单位为每秒像素
         */
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            debug("onFling e1=${e1?.toPrint()} e2=${e2.toPrint()} velocityX=${velocityX} velocityY=${velocityY}")
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onContextClick(e: MotionEvent): Boolean {
            debug("onContextClick e=${e.toPrint()}")
            return super.onContextClick(e)
        }
    }


    // 当前缩放值
    private var currZoom = 1f

    private var minZoom = DEFAULT_MIN_ZOOM
    private var maxZoom = DEFAULT_MAX_ZOOM

    private var zoomAnim = ValueAnimator().apply { duration = DEFAULT_ANIM_DURATION }

    private var drawableWidth = 0f
    private var drawableHeight = 0f

    init {
        gestureDetector = GestureDetector(context, simpleOnGestureListener)

        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawableWidth = drawable.intrinsicWidth.toFloat()
        drawableHeight = drawable.intrinsicHeight.toFloat()
    }

    private fun dealTrans(distanceX: Float, distanceY: Float) {
        suppMatrix.postTranslate(-distanceX, -distanceY)
        applyImageMatrix()
    }

    /**
     * 双击执行缩放动画
     * 这里动画有两种实现方式
     * 1. 起始 matrix，目标matrix，基于变化量求过程中的 matrix
     * 2. 当前 matrix,基于每次变换的差值变化
     */
    private fun dealZoomAnim(e: MotionEvent) {
        // 点击的点设置为缩放的中心点
        val focalPoint = PointF(e.x, e.y)
        // 缩放之后平移的值已改变，不能直接使用
        val dx = suppMatrix.transX()
        val dy = suppMatrix.transY()
        debug("dx=${dx} dy=${dy}}")

        val animatorUpdateListener = object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val tempValue = animation.animatedValue as Float
                suppMatrix.setTranslate(dx, dy)
                suppMatrix.postScale(tempValue, tempValue, focalPoint.x, focalPoint.y)
                debug("tempValue=${tempValue} currScale=${suppMatrix.scaleX()} currMatrix:${suppMatrix.toPrint()}")
                applyImageMatrix()
                currZoom = suppMatrix.scaleX()
            }
        }

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
     * 把变换后的效果应用给 ImageView
     */
    private fun applyImageMatrix() {
        val drawMatrix = Matrix()
        drawMatrix.set(originMatrix)
        // 即当前 Matrix 会乘以传入的 Matrix。
        drawMatrix.postConcat(suppMatrix)
        imageMatrix = drawMatrix
    }


}