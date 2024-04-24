package com.dafay.imageview.overscroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.imageview.R
import com.dafay.imageview.databinding.ActivityMainBinding
import com.dafay.imageview.databinding.ActivityTestOverScrollerBinding

class TestOverScrollerActivity : BaseActivity(R.layout.activity_test_over_scroller) {
    override val binding: ActivityTestOverScrollerBinding by viewBinding()

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