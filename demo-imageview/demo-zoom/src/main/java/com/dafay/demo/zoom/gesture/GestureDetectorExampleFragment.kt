package com.dafay.demo.zoom.gesture

import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentGestureDetectorExampleBinding


class GestureDetectorExampleFragment : BaseFragment(R.layout.fragment_gesture_detector_example) {
    override val binding: FragmentGestureDetectorExampleBinding by viewBinding()


    override fun initViews() {
        super.initViews()
        initTestButtons()
    }

    private fun initTestButtons() {
        binding.cvBtnContainer.addButton("translateX 10px", {
            binding.givImageView.suppMatrix.postTranslate(10f, 0f)
            binding.givImageView.applyToImageMatrix()
        })

        binding.cvBtnContainer.addButton("translateY 10px", {
            binding.givImageView.suppMatrix.postTranslate(0f, 10f)
            binding.givImageView.applyToImageMatrix()
        })

        binding.cvBtnContainer.addButton("模拟双击放大动画过程中，图片切换为高清", {
            binding.givImageView.playZoomAnimTap(-100f, -100f)
            binding.givImageView.postDelayed({
                binding.givImageView.setImageResource(R.mipmap.img_03)
            }, 1000L)

        })

    }
}