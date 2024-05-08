package com.dafay.demo.zoom.matrix

import android.util.DisplayMetrics
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentDensityBinding


class DensityFragment : BaseFragment(R.layout.fragment_density) {
    override val binding: FragmentDensityBinding by viewBinding()


    override fun initViews() {
        super.initViews()
        testDensity()
    }


    private fun testDensity(){
        debug("1.dp2px=${1.dp2px}")
        val metrics = DisplayMetrics()
        requireActivity().windowManager.getDefaultDisplay().getMetrics(metrics)
    }

}