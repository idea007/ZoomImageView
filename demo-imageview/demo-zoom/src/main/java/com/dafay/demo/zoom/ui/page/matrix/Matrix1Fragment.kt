package com.dafay.demo.zoom.ui.page.matrix

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.viewbinding.ViewBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentMatrix1Binding


class Matrix1Fragment : BaseFragment(R.layout.fragment_matrix1) {
    override val binding: FragmentMatrix1Binding by viewBinding()

    override fun initViews() {
        super.initViews()
        binding.sbSeekbar.max=180
        binding.sbSeekbar.progress=90

        binding.sbSeekbar1.max=180
        binding.sbSeekbar1.progress=90
    }

    override fun bindListener() {
        super.bindListener()

        binding.sbSeekbar.setOnSeekBarChangeListener(object:OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.cvCamera.setRotateXDegree(progress-90f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.sbSeekbar1.setOnSeekBarChangeListener(object:OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.cvCamera.setRotateYDegree(progress-90f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })


    }

}