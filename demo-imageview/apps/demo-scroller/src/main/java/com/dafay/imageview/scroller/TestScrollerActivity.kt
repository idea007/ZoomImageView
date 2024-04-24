package com.dafay.imageview.scroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.imageview.R
import com.dafay.imageview.databinding.ActivityMainBinding
import com.dafay.imageview.databinding.ActivityTestScrollerBinding

class TestScrollerActivity : BaseActivity(R.layout.activity_test_scroller) {
    override val binding: ActivityTestScrollerBinding by viewBinding()

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