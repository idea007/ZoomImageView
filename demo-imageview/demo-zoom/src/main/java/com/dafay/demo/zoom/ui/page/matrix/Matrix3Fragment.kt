package com.dafay.demo.zoom.ui.page.matrix

import android.graphics.Matrix
import android.widget.ImageView
import android.widget.SeekBar
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentMatrix3Binding
import com.dafay.demo.zoom.utils.toPrint
import com.dafay.demo.zoom.utils.transX
import com.dafay.demo.zoom.utils.transY


class Matrix3Fragment : BaseFragment(R.layout.fragment_matrix3) {
    override val binding: FragmentMatrix3Binding by viewBinding()

    // 当前 imageview 的 matrix
    private var originMatrix: Matrix = Matrix()

    private val currMatrix = Matrix()

    private var dx = 0f
    private var dy = 0f
    private var zoom = 0f
    private var rotation = 0f

    private var mViewCenterX = 0f
    private var mViewCenterY = 0f
    private var drawableWidth = 0f
    private var drawableHeight = 0f
    override fun initViews() {
        super.initViews()

        binding.ivOriginImage.scaleType = ImageView.ScaleType.MATRIX
        binding.ivOriginImage.setImageResource(R.mipmap.img_01)

        binding.ivOriginImage.post(object : Runnable {
            override fun run() {
                mViewCenterX = binding.ivOriginImage.width / 2f
                mViewCenterY = binding.ivOriginImage.height / 2f
                drawableWidth = binding.ivOriginImage.drawable.intrinsicWidth.toFloat()
                drawableHeight = binding.ivOriginImage.drawable.intrinsicHeight.toFloat()
                MatrixUtils.getImageViewMatrix(binding.ivOriginImage)?.let {
                    originMatrix = it
                }
                debug("viewWidth:${binding.ivOriginImage.width} viewHeight:${binding.ivOriginImage.height}")
                debug("drawableWidth:${binding.ivOriginImage.drawable.intrinsicWidth} drawableHeight:${binding.ivOriginImage.drawable.intrinsicHeight}")
                debug("originMatrix:${originMatrix.toString()}")
            }
        })

        initTestButtons()

    }

    private fun initTestButtons() {
        binding.cvBtnContainer.addButton("translateX 10px", {
            currMatrix.postTranslate(10f, 0f)
            debug("currMatrix:${currMatrix.toPrint()}")
            binding.ivOriginImage.imageMatrix = currMatrix
        })

        binding.cvBtnContainer.addButton("translateX -10px", {
            currMatrix.postTranslate(-10f, 0f)
            debug("currMatrix:${currMatrix.toPrint()}")
            binding.ivOriginImage.imageMatrix = currMatrix
        })

        binding.cvBtnContainer.addButton("postScale 1.1f", {
            currMatrix.postScale(1.1f, 1.1f)
            debug("currMatrix:${currMatrix.toPrint()}")
            binding.ivOriginImage.imageMatrix = currMatrix
        })

        binding.cvBtnContainer.addButton("postScale 0.9f", {
            currMatrix.postScale(0.9f, 0.9f)
            debug("currMatrix:${currMatrix.toPrint()}")
            binding.ivOriginImage.imageMatrix = currMatrix
        })
    }


    override fun bindListener() {
        super.bindListener()
        binding.sbTranslateX.max = 300
        binding.sbTranslateX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currMatrix.setTranslate(progress.toFloat(), currMatrix.transY())
                debug("currMatrix:${currMatrix.toPrint()}")
                binding.ivOriginImage.imageMatrix = currMatrix
                dx = currMatrix.transX()
                dy = currMatrix.transY()
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

                currMatrix.setTranslate(currMatrix.transX(), progress.toFloat())
                debug("currMatrix:${currMatrix.toPrint()}")
                binding.ivOriginImage.imageMatrix = currMatrix
                dx = currMatrix.transX()
                dy = currMatrix.transY()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.sbRotate.max = 360
        binding.sbRotate.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                var thumRotation = progress - rotation
//                rotation = progress.toFloat()
//                currMatrix.postRotate(thumRotation,dx+drawableWidth/2,dy+drawableHeight/2)
//                debug("currMatrix:${currMatrix.toPrint()}")
//                binding.ivOriginImage.imageMatrix=currMatrix

                currMatrix.setRotate(progress.toFloat(), drawableWidth/2,drawableHeight/2)
                rotation = progress.toFloat()
                currMatrix.postTranslate(dx,dy)
                debug("currMatrix:${currMatrix.toPrint()}")
                binding.ivOriginImage.imageMatrix = currMatrix
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
                thumMatix.set(currMatrix)
                thumMatix.postScale(progress / 10f,
                    progress / 10f,dx+drawableWidth/2,dy+drawableHeight/2)
                debug("currMatrix:${thumMatix.toPrint()}")
                binding.ivOriginImage.imageMatrix = thumMatix
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }
}