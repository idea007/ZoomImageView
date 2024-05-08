package com.dafay.demo.zoom.overscroller

import android.view.Choreographer
import android.view.animation.AnimationUtils
import android.widget.OverScroller
import android.widget.Scroller
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentScrollerTrackBinding
import com.dafay.demo.zoom.databinding.FragmentTestOverScrollerTrackBinding

class OverScrollerTrackFragment : BaseFragment(R.layout.fragment_test_over_scroller_track) {
    override val binding: FragmentTestOverScrollerTrackBinding by viewBinding()

    private lateinit var overScroller: OverScroller

    override fun initViews() {
        overScroller = OverScroller(requireContext())
        initTestButtons()
    }

    private fun initTestButtons() {
        binding.cvBtnContainer.addButton("startScroll", {
            overScroller.startScroll(0, 0, 300, 300, 1000)
            startAnim()
        })

        binding.cvBtnContainer.addButton("forceFinished", {
            overScroller.forceFinished(true)
        })

        binding.cvBtnContainer.addButton("fling", {
            overScroller.fling(0, 0, 1000, 1000, 0, 80, 0, 80, 200, 200)
            startAnim()
        })
    }

    /**
     * 开启动画
     */
    private fun startAnim() {
        startTime = AnimationUtils.currentAnimationTimeMillis()
        binding.rgvRate.clearTrack()
        postNextFrame()
    }

    // 动画开启的时间
    private var startTime: Long = 0
    val choreographer = Choreographer.getInstance()
    private fun postNextFrame() {
        val timePassed: Int = (AnimationUtils.currentAnimationTimeMillis() - startTime).toInt()
        val computeScrollOffsetResult = overScroller.computeScrollOffset()
        val currX = overScroller.currX
        val currY = overScroller.currY
        val currVelocity = overScroller.currVelocity
        val startX = overScroller.startX
        val startY = overScroller.startY
        val finalX = overScroller.finalX
        val finalY = overScroller.finalY
        val isFinished = overScroller.isFinished
        val isOverScrolled = overScroller.isOverScrolled
        overScroller.debug(
            "computeScroll: \n" +
                    "computeScrollOffsetResult=${computeScrollOffsetResult} isFinished=${isFinished}\n" +
                    "startX=${startX} startY=${startY} finalX=${finalX} finalY=${finalY}\n" +
                    "currX=${currX} currY=${currY} currVelocity=${currVelocity}\n" +
                    "isOverScrolled=${isOverScrolled}"
        )

        choreographer.postFrameCallback {
            if (overScroller.computeScrollOffset()) {
                binding.vTarget.translationX = currX.toFloat()
                binding.vTarget.translationY = currY.toFloat()
                binding.rgvRate.addTrackPoint(timePassed / 1000f, currX / 300f)
                postNextFrame()
            }
        }
    }

}