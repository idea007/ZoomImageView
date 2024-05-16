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
import android.view.Choreographer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.OverScroller
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.utils.MathUtils
import com.dafay.demo.zoom.utils.scaleX
import com.dafay.demo.zoom.utils.toPrint
import com.dafay.demo.zoom.utils.translateBy
import com.dafay.demo.zoom.utils.zoomBy
import com.dafay.demo.zoom.utils.zoomTo

/**
 * 功能
 * 拖动响应 onfling
 */
class Gesture061ImageView @kotlin.jvm.JvmOverloads constructor(
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

    // 用来执行 onFling 动画
    private lateinit var overScroller: OverScroller


    init {
        overScroller = OverScroller(context)
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
        playZoomAnim(e.x, e.y)
    }

    fun playZoomAnim(pivotX: Float, pivotY: Float) {
        zoomAnim.removeAllUpdateListeners()
        zoomAnim.cancel()

        // 点击的点设置为缩放的支点
        pivotPointF.set(pivotX, pivotY)
        val startZoom = suppMatrix.scaleX()
        val endZoom = if (Math.abs(startZoom - maxZoom) > Math.abs(startZoom - minZoom)) maxZoom else minZoom

        val startMatrix = Matrix(imageMatrix)
        val endMatrix = Matrix(originMatrix).apply {
            val tempSuppMatrix = Matrix(suppMatrix)
            tempSuppMatrix.zoomTo(endZoom, pivotPointF.x, pivotPointF.y)
            this.postConcat(tempSuppMatrix)
        }
        // 边界矫正
        endMatrix.postConcat(correctByViewBound(endMatrix))

        val tmpPointArr = floatArrayOf(pivotX, pivotY)
        MathUtils.computeNewPosition(tmpPointArr, imageMatrix, endMatrix)
        val endPivotPointF = PointF(tmpPointArr[0], tmpPointArr[1])

        debug("playZoomAnim startZoom=${startZoom} endZoom=${endZoom} startPivotPointF:${pivotPointF} endPivotPointF=${endPivotPointF}}")

        testSuppMatrix(startMatrix, pivotPointF, endMatrix, endPivotPointF)

        val animatorUpdateListener = object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val tempValue = animation.animatedValue as Float
                val factor = (tempValue - startZoom) / (endZoom - startZoom)
                debug("playZoomAnim factor=${factor}")
                val currMatrix = MathUtils.interpolate(
                    startMatrix,
                    pivotPointF.x,
                    pivotPointF.y,
                    endMatrix,
                    endPivotPointF.x,
                    endPivotPointF.y,
                    factor
                )
                // suppMatrix * originMatrix = currMatrix;  suppMatrix = currMatrix *（originMatrix 的逆矩阵）
                val tmpMatrix = Matrix()
                originMatrix.invert(tmpMatrix)
                tmpMatrix.postConcat(currMatrix)
                suppMatrix.set(tmpMatrix)
                applyToImageMatrix(true)
            }
        }
        zoomAnim.setFloatValues(startZoom, endZoom)
        zoomAnim.addUpdateListener(animatorUpdateListener)
        zoomAnim.start()
    }

    private fun testSuppMatrix(startMatrix: Matrix, pivotPointF: PointF, endMatrix: Matrix, endPivotPointF: PointF) {

        val currMatrix1 = MathUtils.interpolate(
            startMatrix,
            this.pivotPointF.x,
            this.pivotPointF.y,
            endMatrix,
            endPivotPointF.x,
            endPivotPointF.y,
            0f
        )

        val currMatrix2 = MathUtils.interpolate(
            startMatrix,
            this.pivotPointF.x,
            this.pivotPointF.y,
            endMatrix,
            endPivotPointF.x,
            endPivotPointF.y,
            0.2f
        )

        val currMatrix5 = MathUtils.interpolate(
            startMatrix,
            this.pivotPointF.x,
            this.pivotPointF.y,
            endMatrix,
            endPivotPointF.x,
            endPivotPointF.y,
            0.5f
        )

        val currMatrix10 = MathUtils.interpolate(
            startMatrix,
            this.pivotPointF.x,
            this.pivotPointF.y,
            endMatrix,
            endPivotPointF.x,
            endPivotPointF.y,
            1f
        )

        // 对比 先移动 和 后移动 是否一致
        val suppMatrix = Matrix().apply {
            this.zoomTo(2f, 0f, 0f)
        }
        val matrix1 = Matrix(originMatrix).apply {
            this.postConcat(suppMatrix)
        }
        matrix1.translateBy(10f, 10f)

        val matrix2 = Matrix(originMatrix).apply {
            suppMatrix.translateBy(10f, 10f)
            this.postConcat(suppMatrix)
        }
        debug("matrix1=${matrix1} matrix2=${matrix2}")
    }

    /**
     * test 用
     */
    private fun invertSuppMatrix(currMatrix: Matrix): Matrix {
        val tmpMatrix = Matrix()
        originMatrix.invert(tmpMatrix)
        tmpMatrix.postConcat(currMatrix)
        return tmpMatrix
    }

    /**
     * 处理拖动（平移）事件
     */
    private fun dealOnScroll(distanceX: Float, distanceY: Float) {
        suppMatrix.translateBy(-distanceX, -distanceY)
        applyToImageMatrix()
    }

    /**
     * 处理 onFling
     */
    private fun dealOnFling(e2: MotionEvent, velocityX: Float, velocityY: Float) {
        val rect = getDrawMatrixRect(imageMatrix) ?: return
        val startX = Math.round(-rect.left)
        val minX: Int
        val maxX: Int
        val minY: Int
        val maxY: Int
        if (width < rect.width()) {
            minX = 0
            maxX = Math.round(rect.width() - width)
        } else {
            maxX = startX
            minX = maxX
        }
        val startY = Math.round(-rect.top)
        if (height < rect.height()) {
            minY = 0
            maxY = Math.round(rect.height() - height)
        } else {
            maxY = startY
            minY = maxY
        }
        preX = startX
        preY = startY

        if (!((startX != maxX) || (startY != maxY))) {
            return
        }
        debug("startX=${startX} startY=${startY} velocityX=${velocityX} velocityY=${velocityY} minX=${minX} maxX=${maxX} minY=${minY} maxY=${maxY}")
        overScroller.fling(startX, startY, (velocityX/2).toInt(), (velocityY/2).toInt(), minX, maxX, minY, maxY, 0, 0)
        startFlingAnim()
    }

    private var startTime: Long = 0
    private val choreographer = Choreographer.getInstance()
    private var preX = 0
    private var preY = 0
    private fun startFlingAnim() {
        startTime = AnimationUtils.currentAnimationTimeMillis()
        postNextFrame()
    }

    private fun postNextFrame() {
        debug("overScroller:${overScroller.toPrint()}")
        val currX = overScroller.currX
        val currY = overScroller.currY
        val dx = currX - preX
        val dy = currY - preY
        preX = currX
        preY = currY
        debug("dx=${dx} dy=${dy}")
        suppMatrix.translateBy(dx.toFloat(), dy.toFloat())
        applyToImageMatrix()
        choreographer.postFrameCallback {
            if (overScroller.computeScrollOffset()) {
                postNextFrame()
            }
        }
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

    /**
     * @param srcMatrix 输入 matrix
     * @return outMatrix 输出经过限制之后要移动的 matrix
     */
    private fun correctByViewBound(srcMatrix: Matrix): Matrix {
        val outMatrix = Matrix()
        // 得到 matrix 的 rect
        val tempRectF = getDrawMatrixRect(srcMatrix)
        tempRectF ?: return outMatrix
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
        outMatrix.translateBy(deltaX, deltaY)
        return outMatrix
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
    fun applyToImageMatrix(skipCorrect: Boolean = false) {
        if (!skipCorrect) {
            // 在应用之前，进行边界矫正
            correctSuppMatrix()
        }
        val drawMatrix = Matrix()
        drawMatrix.set(originMatrix)
        // 即当前 Matrix 会乘以传入的 Matrix。 suppMatrix*originMatrix
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

        /**
         * 当发生 fling 事件时，使用初始 on down MotionEvent 和匹配的 up MotionEvent 通知该事件。
         * 计算出的速度沿 x 轴和 y 轴提供，单位为每秒像素
         */
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            debug("onFling e1=${e1?.toPrint()} e2=${e2.toPrint()} velocityX=${velocityX} velocityY=${velocityY}")
            dealOnFling(e2, velocityX, velocityY)
            return super.onFling(e1, e2, velocityX, velocityY)
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