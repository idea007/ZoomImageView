package com.dafay.demo.zoom.ui.page.scroller

import android.view.Choreographer
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Scroller
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentScrollerTrackBinding
import com.dafay.demo.zoom.databinding.FragmentTestScrollerViewBinding
import com.google.android.material.button.MaterialButton

class ScrollerTrackFragment : BaseFragment(R.layout.fragment_scroller_track) {
    override val binding: FragmentScrollerTrackBinding by viewBinding()

    private lateinit var scroller: Scroller

    override fun initViews() {
        scroller = Scroller(requireContext())
        initTestButtons()
    }

    private fun initTestButtons() {
        binding.cvBtnContainer.addButton("startScroll", {
            binding.rgvRate.clearTrack()
            scroller.startScroll(0, 0, 300, 300, 1000)
            postNextFrame()
        })

        binding.cvBtnContainer.addButton("forceFinished",{
            scroller.forceFinished(true)
        })

        binding.cvBtnContainer.addButton("fling",{
            binding.rgvRate.clearTrack()
            scroller.fling(0, 0, 1000, 1000, 0, 150, 0, 150)
            postNextFrame()
        })
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
                binding.rgvRate.addTrackPoint(timePassed / duration.toFloat(), currX/300f)
                postNextFrame()
            }
        }
    }

}