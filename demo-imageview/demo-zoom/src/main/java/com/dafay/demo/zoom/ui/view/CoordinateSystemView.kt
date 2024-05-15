package com.dafay.demo.zoom.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.R

/**
 * 坐标系
 *
 */
class CoordinateSystemView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private var viewWidth = 0f
    private var viewHeight = 0f
    private var centerX = 0f
    private var centerY = 0f

    // 画笔 绘制添加内容
    private val paint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    // 绘制坐标系
    private val coordPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    // 原点偏移量比率
    private var originOffsetRatio = 0f

    // 单位长度比率（[0,1] 映射的区域）
    private var unitLengthRatio = 0.4f

    private val pointList = ArrayList<PointF>()

    private val arrowLength = 8.dp2px

    init {
        resolveAttrs(attrs)
        initPaint()
    }

    private fun resolveAttrs(attrs: AttributeSet?) {
        attrs ?: return
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CoordinateSystemView)
        originOffsetRatio = ta.getFloat(R.styleable.CoordinateSystemView_originOffsetRatio, 0f)
        unitLengthRatio = ta.getFloat(R.styleable.CoordinateSystemView_unitLengthRatio, 0.4f)
        ta.recycle()
    }

    private fun initPaint() {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLUE
        paint.strokeWidth = 4f

        coordPaint.isAntiAlias = true
        coordPaint.style = Paint.Style.STROKE
        coordPaint.color = Color.RED
        coordPaint.strokeWidth = 2f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        debug("onSizeChanged()")
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        centerX = viewWidth / 2
        centerY = viewHeight / 2
    }

    /**
     * 添加轨迹上的点 x∈[0,1]，y∈[0,1]
     */
    fun addPoint(x: Float, y: Float) {
        pointList.add(PointF(x, y))
        invalidate()
    }

    fun clearPoints() {
        pointList.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCoordinateSystem(canvas)
        drawPoints(canvas)
    }

    /**
     * 绘制速率轨迹
     */
    private fun drawPoints(canvas: Canvas) {
        val originOffset = viewWidth * originOffsetRatio
        val unitLength = viewHeight * unitLengthRatio
        canvas.save()
        canvas.translate(centerX + originOffset, centerY + originOffset) // 将坐标系移动到画布中央
        for (i in 0 until pointList.size) {
            canvas.drawPoint(
                pointList[i].x * unitLength,
                pointList[i].y * unitLength,
                paint
            )
        }
        canvas.restore()
    }

    /**
     * 绘制坐标系(x,y轴)
     */
    private fun drawCoordinateSystem(canvas: Canvas) {
        val originOffset = viewWidth * originOffsetRatio
        val unitLength = viewHeight * unitLengthRatio
        canvas.save() // 绘制做坐标系
        // 将坐标系移动到画布中央
        canvas.translate(centerX + originOffset, centerY + originOffset)
        coordPaint.strokeWidth = 2f
        coordPaint.alpha = 255
        // 绘制 x 轴
        canvas.drawLine(-(centerX + originOffset), 0f, -(centerX + originOffset) + viewWidth, 0f, coordPaint)
        val tempPoint = PointF().apply {
            x = Math.cos(Math.toRadians(135.0)).toFloat() * arrowLength + (-(centerX + originOffset) + viewWidth)
            y = Math.sin(Math.toRadians(135.0)).toFloat() * arrowLength
        }
        canvas.drawLine(-(centerX + originOffset) + viewWidth, 0f, tempPoint.x, tempPoint.y, coordPaint)
        tempPoint.apply {
            x = Math.cos(Math.toRadians(225.0)).toFloat() * arrowLength + (-(centerX + originOffset) + viewWidth)
            y = Math.sin(Math.toRadians(225.0)).toFloat() * arrowLength
        }
        canvas.drawLine(-(centerX + originOffset) + viewWidth, 0f, tempPoint.x, tempPoint.y, coordPaint)
        // 绘制 y 轴
        canvas.drawLine(0f, -(centerY + originOffset), 0f, -(centerY + originOffset) + viewHeight, coordPaint)
        tempPoint.apply {
            x = Math.cos(Math.toRadians(315.0)).toFloat() * arrowLength
            y = Math.sin(Math.toRadians(315.0)).toFloat() * arrowLength - (centerY + originOffset) + viewHeight
        }
        canvas.drawLine(0f, -(centerY + originOffset) + viewHeight, tempPoint.x, tempPoint.y, coordPaint)
        tempPoint.apply {
            x = Math.cos(Math.toRadians(225.0)).toFloat() * arrowLength
            y = Math.sin(Math.toRadians(225.0)).toFloat() * arrowLength - (centerY + originOffset) + viewHeight
        }
        canvas.drawLine(0f, -(centerY + originOffset) + viewHeight, tempPoint.x, tempPoint.y, coordPaint)
        coordPaint.strokeWidth = 2f
        coordPaint.alpha = 150
        // 绘制刻度
        val tickSpace = unitLength / 10f  // 刻度间隙
        val pointStart = PointF()
        val pointEnd = PointF()
        // 横轴刻度
        var i = tickSpace
        while (i < centerX - originOffset) {
            pointStart.set(i, if (i == unitLength) -8.dp2px.toFloat() else -3.dp2px.toFloat())
            pointEnd.set(i, if (i == unitLength) 8.dp2px.toFloat() else 3.dp2px.toFloat())
            canvas.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, coordPaint)
            i += tickSpace
        }
        i = -tickSpace
        while (i > -(centerX + originOffset)) {
            // 横轴刻度
            pointStart.set(i, if (i == -unitLength) -8.dp2px.toFloat() else -3.dp2px.toFloat())
            pointEnd.set(i, if (i == -unitLength) 8.dp2px.toFloat() else 3.dp2px.toFloat())
            canvas.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, coordPaint)
            i -= tickSpace
        }
        i = tickSpace
        while (i < centerY - originOffset) {
            // 横轴刻度
            pointStart.set(if (i == unitLength) -8.dp2px.toFloat() else -3.dp2px.toFloat(), i)
            pointEnd.set(if (i == unitLength) 8.dp2px.toFloat() else 3.dp2px.toFloat(), i)
            canvas.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, coordPaint)
            i += tickSpace
        }
        i = -tickSpace
        while (i > -(centerY + originOffset)) {
            // 横轴刻度
            pointStart.set(if (i == -unitLength) -8.dp2px.toFloat() else -3.dp2px.toFloat(), i)
            pointEnd.set(if (i == -unitLength) 8.dp2px.toFloat() else 3.dp2px.toFloat(), i)
            canvas.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, coordPaint)
            i -= tickSpace
        }
        coordPaint.strokeWidth = 1f
        coordPaint.alpha = 50
        if (unitLength < (-(centerX + originOffset) + viewWidth)) {
            canvas.drawLine(unitLength, -viewHeight, unitLength, viewHeight, coordPaint)
        }
        if (-unitLength > -(centerX + originOffset)) {
            canvas.drawLine(-unitLength, -viewHeight, -unitLength, viewHeight, coordPaint)
        }
        if (unitLength < -(centerY + originOffset) + viewHeight) {
            canvas.drawLine(-viewWidth, unitLength, viewWidth, unitLength, coordPaint)
        }
        if (-unitLength > -(centerY + originOffset)) {
            canvas.drawLine(-viewWidth, -unitLength, viewWidth, -unitLength, coordPaint)
        }
        canvas.restore()
    }
}