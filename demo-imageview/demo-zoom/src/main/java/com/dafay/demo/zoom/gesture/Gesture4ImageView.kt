package com.dafay.demo.zoom.gesture

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.utils.toPrint

/**
 * TODO:
 * 演示
 * 1. 图片双击放大
 * 2. 移到
 */
class Gesture4ImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // 手势检测器
    private val gestureDetector: GestureDetector
    private val originMatrix=Matrix()
    private val currMatrix=Matrix()

    private val simpleOnGestureListener =object:GestureDetector.SimpleOnGestureListener(){
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
            if(currZoom<maxZoom){
                // 放大
                currMatrix.postScale(maxZoom,maxZoom,e.x,e.y)
                debug("currMatrix:${currMatrix.toPrint()}")
                imageMatrix=currMatrix
                currZoom=maxZoom
            }else{
                // 缩小
                currMatrix.postScale(minZoom,minZoom,e.x,e.y)
                debug("currMatrix:${currMatrix.toPrint()}")
                imageMatrix=currMatrix
                currZoom=minZoom
            }
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

        override fun onContextClick(e: MotionEvent): Boolean {
            debug("onContextClick e=${e.toPrint()}")
            return super.onContextClick(e)
        }
    }

    // 当前缩放值
    private var currZoom=1f

    private var minZoom = DEFAULT_MIN_ZOOM
    private var maxZoom = DEFAULT_MAX_ZOOM

    init {
        gestureDetector = GestureDetector(context,simpleOnGestureListener)

        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(event)
            }
        })
    }


}