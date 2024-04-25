package com.dafay.demo.gesture.detector

import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.gesture.detector.databinding.ActivityMainBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val binding: ActivityMainBinding by viewBinding()

}