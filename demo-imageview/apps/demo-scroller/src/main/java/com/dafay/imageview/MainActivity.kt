package com.dafay.imageview

import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.imageview.databinding.ActivityMainBinding

class MainActivity : BaseActivity(R.layout.activity_main) {

    override val binding: ActivityMainBinding by viewBinding()

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