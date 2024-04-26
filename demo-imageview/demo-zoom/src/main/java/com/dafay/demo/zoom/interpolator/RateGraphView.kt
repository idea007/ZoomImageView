package com.dafay.demo.zoom.interpolator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.lib.base.utils.dp2px

/**
 * 显示速率
 */
class RateGraphView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private var viewWidth = 0f
    private var viewHeight = 0f
    private var centerX = 0f
    private var centerY = 0f

    // 画笔
    private val paint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val coordPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    // 原点偏移量
    private val originPointOffset = 50.dp2px.toFloat()

    private val trackList = ArrayList<PointF>()

    init {
        initPaint()
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
     * 添加轨迹上的点 x[0~1] y[插值器输出]
     */
    fun addTrackPoint(x: Float, y: Float) {
        trackList.add(PointF(x, y))
        invalidate()
    }

    fun clearTrack() {
        trackList.clear()
        initPaint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCoordinateSystem(canvas)
        drawTrack(canvas)
    }

    /**
     * 绘制速率轨迹
     */
    private fun drawTrack(canvas: Canvas) {
        canvas.save()
        canvas.translate(50.dp2px.toFloat(), viewHeight - 50.dp2px.toFloat()) // 将坐标系移动到画布中央
        canvas.scale(1f, -1f) // 翻转Y轴
        for (i in 0 until trackList.size) {
            canvas.drawPoint(trackList[i].x*(viewWidth - 2 * originPointOffset) , trackList[i].y * (viewWidth - 2 * originPointOffset), paint)
        }
        canvas.restore()
    }


    // 绘制坐标系(x,y轴)
    private fun drawCoordinateSystem(canvas: Canvas) {
        canvas.save() // 绘制做坐标系
        canvas.translate(50.dp2px.toFloat(), viewHeight - 50.dp2px.toFloat()) // 将坐标系移动到画布中央
        canvas.scale(1f, -1f) // 翻转Y轴
        coordPaint.strokeWidth=2f
        coordPaint.alpha=255
        canvas.drawLine(0f, -originPointOffset, 0f, viewHeight, coordPaint)
        canvas.drawLine(-originPointOffset, 0f, viewWidth, 0f, coordPaint)
        val tickSpace = (viewWidth - 2 * originPointOffset) / 10f
        val pointStart = PointF()
        val pointEnd = PointF()
        for (i in 1 .. 10) {
            // 横轴刻度
            pointStart.set(i * tickSpace - coordPaint.strokeWidth / 2, 0f)
            pointEnd.set(i * tickSpace - coordPaint.strokeWidth / 2, 8.dp2px.toFloat())
            canvas.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, coordPaint)
            // 纵轴刻度
            pointStart.set(0f, i * tickSpace - coordPaint.strokeWidth / 2)
            pointEnd.set(8.dp2px.toFloat(), i * tickSpace - coordPaint.strokeWidth / 2)
            canvas.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, coordPaint)
        }

        coordPaint.strokeWidth=1f
        coordPaint.alpha=40
        canvas.drawLine((viewWidth - 2 * originPointOffset), -originPointOffset, (viewWidth - 2 * originPointOffset), viewHeight, coordPaint)
        canvas.drawLine(-originPointOffset, (viewWidth - 2 * originPointOffset), viewWidth, (viewWidth - 2 * originPointOffset), coordPaint)

        canvas.restore()
    }


}