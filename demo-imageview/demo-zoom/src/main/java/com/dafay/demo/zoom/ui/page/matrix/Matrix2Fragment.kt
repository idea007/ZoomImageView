package com.dafay.demo.zoom.ui.page.matrix

import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentMatrix2Binding


class Matrix2Fragment : BaseFragment(R.layout.fragment_matrix2) {
    override val binding: FragmentMatrix2Binding by viewBinding()


    override fun bindListener() {
        super.bindListener()

        binding.group.setOnCheckedChangeListener(object:OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                    R.id.point0 -> binding.poly.setTestPoint(0)
                    R.id.point1 -> binding.poly.setTestPoint(1)
                    R.id.point2 -> binding.poly.setTestPoint(2)
                    R.id.point3 -> binding.poly.setTestPoint(3)
                    R.id.point4 -> binding.poly.setTestPoint(4)
                }
            }
        })
    }
}