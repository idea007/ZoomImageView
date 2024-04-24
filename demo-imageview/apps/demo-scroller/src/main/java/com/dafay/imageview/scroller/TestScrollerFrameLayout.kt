package com.dafay.imageview.scroller

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.Scroller
import com.dafay.demo.lib.base.utils.debug

class TestScrollerFrameLayout @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private  var scroller: Scroller

    init {
        scroller = Scroller(context)
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
            scrollTo(scroller.getCurrX(), scroller.getCurrY())
            postInvalidate()
        }
    }

}