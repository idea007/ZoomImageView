package com.dafay.demo.zoom.ui.page.scroller

import android.view.Choreographer
import android.widget.Scroller
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentScrollerTrackBinding
import com.dafay.demo.zoom.utils.toPrint

class ScrollerTrackFragment : BaseFragment(R.layout.fragment_scroller_track) {
    override val binding: FragmentScrollerTrackBinding by viewBinding()

    private lateinit var scroller: Scroller

    override fun initViews() {
        scroller = Scroller(requireContext())
        initTestButtons()
    }

    /**
     * 基于快速滑动手势开始滚动。行进的距离取决于投掷的初始速度。参数： startX – 滚动的起始点 (X) startY – 滚动的起始点 (Y) VelocityX – 以每秒像素为单位测量的 fling (X) 的初始速度。 velocityY – 以每秒像素为单位测量的 fling (Y) 初始速度 minX – 最小 X 值。滚动条不会滚动超过该点。 maxX – 最大 X 值。滚动条不会滚动超过该点。 minY – 最小 Y 值。滚动条不会滚动超过该点。 maxY – 最大 Y 值。滚动条不会滚动超过该点。
     */

    private fun initTestButtons() {
        binding.cvBtnContainer.addButton("startScroll", {
            binding.csvCoord.clearPoints()
            scroller.startScroll(0, 0, 300, 300, 1000)
            postNextFrame()
        })

        binding.cvBtnContainer.addButton("forceFinished", {
            scroller.forceFinished(true)
        })

        binding.cvBtnContainer.addButton("fling", {
            binding.csvCoord.clearPoints()
            scroller.fling(0, 0, 1000, 1000, 0, 150, 0, 150)
            postNextFrame()
        })
    }

    private val choreographer = Choreographer.getInstance()
    private fun postNextFrame() {
        debug("scroller:${scroller.toPrint()}")
        choreographer.postFrameCallback {
            if (scroller.computeScrollOffset()) {
                binding.vTarget.translationX = scroller.currX.toFloat()
                binding.vTarget.translationY = scroller.currX.toFloat()
                binding.csvCoord.addPoint(scroller.timePassed() / scroller.duration.toFloat(), scroller.currX / 300f)
                postNextFrame()
            }
        }
    }

}