package com.dafay.imageview

import android.content.Intent
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.imageview.databinding.ActivityMainBinding
import com.dafay.imageview.overscroller.TestOverScrollerActivity
import com.dafay.imageview.overscroller.TestOverScrollerTrackActivity
import com.dafay.imageview.scroller.TestScrollerActivity
import com.dafay.imageview.scroller.TestScrollerTrackActivity

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val binding: ActivityMainBinding by viewBinding()
    override fun bindListener() {
        super.bindListener()
        binding.btnJumpScrollerView.setOnClickListener {
            startActivity(Intent(this, TestScrollerActivity::class.java))
        }
        binding.btnJumpScroller.setOnClickListener {
            startActivity(Intent(this, TestScrollerTrackActivity::class.java))
        }
        binding.btnJumpOverScrollerView.setOnClickListener {
            startActivity(Intent(this, TestOverScrollerActivity::class.java))
        }
        binding.btnJumpOverScroller.setOnClickListener {
            startActivity(Intent(this, TestOverScrollerTrackActivity::class.java))
        }
    }
}