package com.dafay.demo.zoom.imageview

import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.demo.zoom.imageview.databinding.ActivityMainBinding

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val binding: ActivityMainBinding by viewBinding()

    override fun initViews() {
        super.initViews()
        binding.zivImageview.setImageResource(R.mipmap.img_01)
    }

}