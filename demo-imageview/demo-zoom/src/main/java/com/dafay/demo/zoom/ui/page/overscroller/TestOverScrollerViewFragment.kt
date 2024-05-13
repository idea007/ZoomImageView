package com.dafay.demo.zoom.ui.page.overscroller

import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentTestOverScrollerBinding
import com.dafay.demo.zoom.databinding.FragmentTestScrollerViewBinding

class TestOverScrollerViewFragment : BaseFragment(R.layout.fragment_test_over_scroller) {
    override val binding: FragmentTestOverScrollerBinding by viewBinding()

    override fun bindListener() {
        super.bindListener()
        binding.btnStart.setOnClickListener {
            binding.flScrollerTest.startScroll(300, 300, 4000)
        }
        binding.btnForceFinished.setOnClickListener {
            binding.flScrollerTest.forceFinished(true)
        }
        binding.btnSpringBack.setOnClickListener {
            binding.flScrollerTest.springBack(50,100,50,100)
        }
    }

}