package com.dafay.demo.zoom.ui.page.overscroller

import android.view.Choreographer
import android.view.animation.AnimationUtils
import android.widget.OverScroller
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentTestOverScrollerTrackBinding
import com.dafay.demo.zoom.utils.toPrint

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


            // overScroller.fling(0, 0, 1000, 1000, 0, 80, 0, 80, 200, 200)

            // velocityX=-4351.1235 velocityY=-342.71777
            // overScroller.fling(0, 0, -4351, -342, -5000, 5000, -5000, 5000, 0, 0)

            // startX=3924 startY=857 velocityX=-3966 velocityY=-1048 minX=0 maxX=4360 minY=0 maxY=1954
            overScroller.fling(3924, 857, -3966, -1048, -5000, 5000, -5000, 5000, 0, 0)
            startAnim()
        })
    }

    /**
     * 开启动画
     */
    private fun startAnim() {
        startTime = AnimationUtils.currentAnimationTimeMillis()
        binding.csvCoord.clearPoints()
        postNextFrame()
    }

    // 动画开启的时间
    private var startTime: Long = 0
    private val choreographer = Choreographer.getInstance()
    private fun postNextFrame() {
        val timePassed: Int = (AnimationUtils.currentAnimationTimeMillis() - startTime).toInt()
        debug("overScroller:${overScroller.toPrint()}")
        choreographer.postFrameCallback {
            if (overScroller.computeScrollOffset()) {
                binding.vTarget.translationX = overScroller.currX.toFloat()/10
                binding.vTarget.translationY = overScroller.currX.toFloat()/10
                binding.csvCoord.addPoint(timePassed / 1000f, overScroller.currX / 1000f)
                postNextFrame()
            }
        }
    }
}