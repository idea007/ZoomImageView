package com.dafay.demo.zoom.matrix

import android.graphics.Matrix
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentMatrix1Binding
import com.dafay.demo.zoom.databinding.FragmentMatrix3Binding


class Matrix3Fragment : BaseFragment(R.layout.fragment_matrix3) {
    override val binding: FragmentMatrix3Binding by viewBinding()


    private var originMatrix: Matrix? = null
    private val matrixValues = FloatArray(9)

    private var dx = 0f
    private var dy = 0f
    private var zoom = 0f
    private var rotation = 0f

    private var mViewCenterX = 0f
    private var mViewCenterY = 0f
    override fun initViews() {
        super.initViews()

        binding.ivOriginImage.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.ivOriginImage.setImageResource(R.mipmap.img_4)

        binding.ivOriginImage.post(object : Runnable {
            override fun run() {
                mViewCenterX = binding.ivOriginImage.width / 2f
                mViewCenterY = binding.ivOriginImage.height / 2f
                originMatrix = MatrixUtils.getImageViewMatrix(binding.ivOriginImage)
                originMatrix?.getValues(matrixValues)
            }
        })

    }

    override fun bindListener() {
        super.bindListener()
        binding.sbTranslateX.max = 300
        binding.sbTranslateX.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                var thumDx = progress.toFloat() - dx
                dx = progress.toFloat()
                originMatrix?.postTranslate(thumDx, 0f)
                ImageViewUtils.animateTransform(binding.ivOriginImage, originMatrix)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.sbTranslateY.max = 300
        binding.sbTranslateY.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var thumDy = progress.toFloat() - dy
                dy = progress.toFloat()
                originMatrix?.postTranslate(0f, thumDy)
                ImageViewUtils.animateTransform(binding.ivOriginImage, originMatrix)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.sbRotate.max = 180
        binding.sbRotate.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var thumRotation = progress - rotation
                rotation = progress.toFloat()
                originMatrix?.postRotate(thumRotation, mViewCenterX, mViewCenterY)
                ImageViewUtils.animateTransform(binding.ivOriginImage, originMatrix)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        binding.sbZoom.setProgress(10)
        binding.sbZoom.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                var thumMatix = Matrix()
                thumMatix.setValues(matrixValues)
                thumMatix?.postTranslate(dx, dy)
                thumMatix?.postRotate(rotation, mViewCenterX, mViewCenterY)
                thumMatix?.postScale(
                    progress / 10f,
                    progress / 10f,
                    mViewCenterX,
                    mViewCenterY
                )
                ImageViewUtils.animateTransform(binding.ivOriginImage, thumMatix)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }
}