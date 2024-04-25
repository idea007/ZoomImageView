package com.dafay.demo.gesture.detector

import android.view.GestureDetector
import android.view.MotionEvent
import com.dafay.demo.lib.base.utils.debug


class MyGestureDetector : GestureDetector.SimpleOnGestureListener(){

    override fun onDown(e: MotionEvent): Boolean {
        debug("onDown e=${e.toPrint()}")
        return false
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
        // debug("onDoubleTapEvent")
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        debug("onDoubleTapEvent e=${e.toPrint()}")
        return super.onDoubleTap(e)
    }

    /**
     * 当滚动发生时，初始 on down MotionEvent 和当前移动 MotionEvent 发生时发出通知。
     * 为方便起见，还提供了 x 和 y 中的距离
     * @param distanceX 自上次调用 onScroll 以来沿 X 轴滚动的距离。这不是 e1(是 down) 和 e2 之间的距离。
     */
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        debug("onScroll e1=${e1?.toPrint()} e2=${e2.toPrint()} distanceX=${distanceX} distanceY=${distanceY}")
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


}