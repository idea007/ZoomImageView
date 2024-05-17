package com.dafay.demo.zoom.ui.page.gesture

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
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
import com.dafay.demo.zoom.utils.toPrint
import com.dafay.demo.zoom.utils.translateBy
import com.dafay.demo.zoom.utils.zoomBy
import com.dafay.demo.zoom.utils.zoomTo

/**
 * 功能
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

    // scaleType matrix，计算图片显示类似 fitCenter 效果时的矩阵
    private val originMatrix = Matrix()
    val suppMatrix = Matrix()

    private var minZoom = com.dafay.demo.zoom.ui.page.gesture.DEFAULT_MIN_ZOOM
    private var maxZoom = com.dafay.demo.zoom.ui.page.gesture.DEFAULT_MAX_ZOOM
    private var zoomAnim = ValueAnimator().apply { duration =
        com.dafay.demo.zoom.ui.page.gesture.DEFAULT_ANIM_DURATION
    }

    init {
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

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        debug("setImageDrawable")
        updateOriginMatrix(drawable)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        debug("setImageBitmap")
        updateOriginMatrix(drawable)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        debug("setImageResource")
        updateOriginMatrix(drawable)
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        debug("setImageURI")
        updateOriginMatrix(drawable)
    }

    /**
     *
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        debug("onSizeChanged")
        updateOriginMatrix(drawable)
    }

    /**
     * TODO: 1. 暂时只处理类 fitCenter 显示这一种，2. 忽视 pading
     * 计算图片显示类似 fitCenter 效果时的矩阵
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
     * 处理双击事件
     * 双击执行缩放动画
     */
    private fun dealOnDoubleTap(e: MotionEvent) {
        playZoomAnimTap(e.x, e.y)
    }

    fun playZoomAnimTap(pivotX: Float, pivotY: Float) {
        // 点击的点设置为缩放的中心点
        pivotPointF.apply {
            x = pivotX
            y = pivotY
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
     * 在显示之前，进行边界矫正，对 suppMatrix 进行调整
     * TODO: 不考虑 padding，不考虑 scaleType
     */
    private fun correctMatrixBound() {
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
            // 新奇的写法
            tempRect[0f, 0f, d.intrinsicWidth.toFloat()] = d.intrinsicHeight.toFloat()
            debug("getDrawMatrixRect tempRect=${tempRect}")
            matrix.mapRect(tempRect)
            debug("getDrawMatrixRect tempRect=${tempRect}")
            return tempRect
        }
        return null
    }


    /**
     * 应用于 ImageView 的 matrix
     * 为了思路清晰，这里频繁创建对象 drawMatrix
     */
    fun applyToImageMatrix() {
        // 在应用之前，进行边界矫正
        correctMatrixBound()
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
            debug("onScaleBegin detector=${detector.toPrint()}")
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            debug("onScaleEnd detector=${detector.toPrint()}")
        }
    }


}