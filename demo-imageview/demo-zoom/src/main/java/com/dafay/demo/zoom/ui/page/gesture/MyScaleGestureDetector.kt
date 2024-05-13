package com.dafay.demo.zoom.ui.page.gesture

import android.view.ScaleGestureDetector
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.utils.toPrint

/**
 * ScaleGestureDetector 用于在手势发生时接收通知的侦听器
 *
 */
class MyScaleGestureDetector :  ScaleGestureDetector.OnScaleGestureListener {


    /**
     * 响应正在进行的手势的缩放事件。通过指针运动报告。
     * 参数： detector – 报告事件的检测器 - 使用它来检索有关事件状态的扩展信息。
     *
     * 返回：检测器是否应将此事件视为已处理。如果未处理事件，检测器将继续累积移动，直到处理事件。
     * 例如，如果应用程序只想在更改大于 0.01 时更新比例因子，则此功能非常有用。
     */
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        detector.
        debug("onScale detector=${detector.toPrint()}")
        return true
    }

    /**
     * 响应缩放手势的开始。由新的指针向下报告。
     * 参数： detector – 报告事件的检测器 - 使用它来检索有关事件状态的扩展信息。
     * 返回：检测器是否应继续识别此手势。例如，如果手势的开头是位于有意义的区域之外的焦点，则 onScaleBegin（） 可能会返回 false 以忽略手势的其余部分。
     */
    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        debug("onScaleBegin detector=${detector.toPrint()}")
        return true
    }

    /**
     * 响应刻度手势的结束。由现有指针向上报告。
     * 缩放结束后，getFocusX（） 和 getFocusY（） 将返回屏幕上剩余指针的焦点。
     * 参数： detector – 报告事件的检测器 - 使用它来检索有关事件状态的扩展信息。
     */
    override fun onScaleEnd(detector: ScaleGestureDetector) {
        debug("onScaleEnd detector=${detector.toPrint()}")
    }


}