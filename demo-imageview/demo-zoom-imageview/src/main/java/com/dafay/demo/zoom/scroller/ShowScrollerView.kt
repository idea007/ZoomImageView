package com.dafay.demo.zoom.scroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.widget.Scroller
import com.dafay.demo.lib.base.utils.debug

class ShowScrollerView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var scroller: Scroller

    private var viewWidth = 0f
    private var viewHeight = 0f
    private var centerX = 0f

    // 画笔
    private val paint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }        //画笔

    private val trackList = ArrayList<PointF>()

    init {
        scroller = Scroller(context)
        initPaint()
    }

    private fun initPaint() {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 4f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        debug("onSizeChanged()")
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        centerX = viewWidth / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTrack(canvas)
    }

    private fun drawTrack(canvas: Canvas) {
        canvas.save()
        canvas.translate(10f, 10f)
        for (i in 0 until trackList.size) {
            if (i % 4 != 0) {
                continue
            }
            canvas.drawPoint(trackList[i].x * 2, trackList[i].y * 2, paint)
        }

        canvas.restore()
    }

    fun startScroll(deltaX: Int, deltaY: Int, duration: Int) {
        scroller.startScroll(scrollX, scrollY, deltaX, deltaY, duration)
        debug("startScroll scrollX=${scrollX} scrollY=${scrollY} deltaX=${deltaX} deltaY=${deltaY}")
        invalidate() // 触发重绘
    }

    fun forceFinished(finished: Boolean) {
        scroller.forceFinished(finished)
        invalidate()
    }

    override fun computeScroll() {
        val computeScrollOffsetResult = scroller.computeScrollOffset()
        val currX = scroller.currX
        val currY = scroller.currY
        val currVelocity = scroller.currVelocity
        val startX = scroller.startX
        val startY = scroller.startY
        val finalX = scroller.finalX
        val finalY = scroller.finalY
        val duration = scroller.duration
        val timePassed = scroller.timePassed()
        val isFinished = scroller.isFinished
        debug(
            "computeScroll: \n" +
                    "computeScrollOffsetResult=${computeScrollOffsetResult} isFinished=${isFinished}\n" +
                    "currX=${currX} currY=${currY} currVelocity=${currVelocity}\n" +
                    "startX=${startX} startY=${startY} finalX=${finalX} finalY=${finalY}\n" +
                    "duration=${duration} timePassed=${timePassed}"
        )
        if (computeScrollOffsetResult) {
            trackList.add(PointF(currX.toFloat(), currY.toFloat()))
            postInvalidate()
        }
    }

}