package com.dafay.demo.zoom.scroller

import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentTestScrollerViewBinding

class TestScrollerViewFragment : BaseFragment(R.layout.fragment_test_scroller_view) {
    override val binding: FragmentTestScrollerViewBinding by viewBinding()

    override fun bindListener() {
        super.bindListener()
        binding.btnStart.setOnClickListener {
            binding.flScrollerTest.startScroll(300, 300, 4000)
            binding.ssvScroller.startScroll(300, 300, 4000)
        }
        binding.btnForceFinished.setOnClickListener {
            binding.flScrollerTest.forceFinished(true)
            binding.ssvScroller.forceFinished(true)
        }
    }

}