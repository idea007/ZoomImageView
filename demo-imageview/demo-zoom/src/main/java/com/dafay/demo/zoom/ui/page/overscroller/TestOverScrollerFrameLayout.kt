package com.dafay.demo.zoom.ui.page.overscroller

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.OverScroller
import com.dafay.demo.lib.base.utils.debug

class TestOverScrollerFrameLayout @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var overScroller: OverScroller

    init {
        overScroller = OverScroller(context)
    }

    fun startScroll(deltaX: Int, deltaY: Int, duration: Int) {
        overScroller.startScroll(scrollX, scrollY, deltaX, deltaY, duration)
        debug("startScroll scrollX=${scrollX} scrollY=${scrollY} deltaX=${deltaX} deltaY=${deltaY}")
        invalidate()
    }

    fun springBack(minX: Int, maxX: Int, minY: Int, maxY: Int) {
        overScroller.springBack(scrollX, scrollY, minX, maxX, minY, maxY)
        debug("springBack scrollX=${scrollX} scrollY=${scrollY} minX=${minX} maxX=${maxX}  minY=${minY} maxY=${maxY}")
        invalidate()
    }

    fun forceFinished(finished: Boolean) {
        overScroller.forceFinished(finished)
        invalidate()
    }

    override fun computeScroll() {
        val computeScrollOffsetResult = overScroller.computeScrollOffset()
        val currX = overScroller.currX
        val currY = overScroller.currY
        val currVelocity = overScroller.currVelocity
        val startX = overScroller.startX
        val startY = overScroller.startY
        val finalX = overScroller.finalX
        val finalY = overScroller.finalY
        val isOverScrolled = overScroller.isOverScrolled
        val isFinished = overScroller.isFinished
        debug(
            "computeScroll: \n" +
                    "computeScrollOffsetResult=${computeScrollOffsetResult} isFinished=${isFinished}\n" +
                    "currX=${currX} currY=${currY} currVelocity=${currVelocity}\n" +
                    "startX=${startX} startY=${startY} finalX=${finalX} finalY=${finalY}\n" +
                    "isOverScrolled=${isOverScrolled}"
        )
        if (computeScrollOffsetResult) {
            scrollTo(overScroller.getCurrX(), overScroller.getCurrY())
            postInvalidate()
        }
    }

}