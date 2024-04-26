package com.dafay.demo.zoom.overscroller

import android.view.Choreographer
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
    }

    override fun bindListener() {
        super.bindListener()

        binding.btnStart.setOnClickListener {
            overScroller.startScroll(0, 0, 300, 300, 5000)
            postNextFrame()
        }

        binding.btnForceFinished.setOnClickListener {
            overScroller.forceFinished(true)
        }

        binding.btnFling.setOnClickListener {
            overScroller.fling(0, 0, 1000, 1000, 0, 80, 0, 80,200,200)
            postNextFrame()
        }
    }

    val choreographer = Choreographer.getInstance()
    private fun postNextFrame() {
        val computeScrollOffsetResult = overScroller.computeScrollOffset()
        val currX = overScroller.currX
        val currY = overScroller.currY
        val currVelocity = overScroller.currVelocity
        val startX = overScroller.startX
        val startY = overScroller.startY
        val finalX = overScroller.finalX
        val finalY = overScroller.finalY
        val isFinished = overScroller.isFinished
        val isOverScrolled=overScroller.isOverScrolled
        debug(
            "computeScroll: \n" +
                    "computeScrollOffsetResult=${computeScrollOffsetResult} isFinished=${isFinished}\n" +
                    "startX=${startX} startY=${startY} finalX=${finalX} finalY=${finalY}\n" +
                    "currX=${currX} currY=${currY} currVelocity=${currVelocity}\n"+
                    "isOverScrolled=${isOverScrolled}"
        )

        choreographer.postFrameCallback {
            if (overScroller.computeScrollOffset()) {
                binding.vTarget.translationX = currX.toFloat()
                binding.vTarget.translationY = currY.toFloat()
                postNextFrame()
            }
        }
    }

}