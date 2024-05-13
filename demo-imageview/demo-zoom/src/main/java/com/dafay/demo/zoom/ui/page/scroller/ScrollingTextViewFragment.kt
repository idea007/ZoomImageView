package com.dafay.demo.zoom.ui.page.scroller

import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentScrollingTextViewBinding


class ScrollingTextViewFragment : BaseFragment(R.layout.fragment_scrolling_text_view) {
    override val binding: FragmentScrollingTextViewBinding by viewBinding()

    private var index = 997

    override fun initViews() {
        super.initViews()
        binding.atvText.setText(index.toString())
    }

    override fun bindListener() {
        super.bindListener()

        binding.btnAdd.setOnClickListener {
            binding.atvText.setText(index.toString(), (++index).toString())
        }

        binding.btnMinus.setOnClickListener {
            binding.atvText.setText(index.toString(), (--index).toString())
        }
    }

}