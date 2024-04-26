package com.dafay.demo.zoom.scroller

import android.view.Choreographer
import android.widget.Scroller
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentScrollerTrackBinding
import com.dafay.demo.zoom.databinding.FragmentTestScrollerViewBinding

class ScrollerTrackFragment : BaseFragment(R.layout.fragment_scroller_track) {
    override val binding: FragmentScrollerTrackBinding by viewBinding()

    private lateinit var scroller: Scroller

    override fun initViews() {
        scroller = Scroller(requireContext())
    }

    override fun bindListener() {
        super.bindListener()

        binding.btnStart.setOnClickListener {
            scroller.startScroll(0, 0, 300, 300, 5000)
            postNextFrame()
        }

        binding.btnForceFinished.setOnClickListener {
            scroller.forceFinished(true)
        }

        binding.btnFling.setOnClickListener {
            scroller.fling(0, 0, 1000, 1000, 0, 50, 0, 50)
            postNextFrame()
        }
    }

    val choreographer = Choreographer.getInstance()
    private fun postNextFrame() {
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
                    "startX=${startX} startY=${startY} finalX=${finalX} finalY=${finalY}\n" +
                    "currX=${currX} currY=${currY} currVelocity=${currVelocity}\n" +
                    "duration=${duration} timePassed=${timePassed}"
        )

        choreographer.postFrameCallback {
            if (scroller.computeScrollOffset()) {
                binding.vTarget.translationX = currX.toFloat()
                binding.vTarget.translationY = currY.toFloat()
                postNextFrame()
            }
        }
    }

}