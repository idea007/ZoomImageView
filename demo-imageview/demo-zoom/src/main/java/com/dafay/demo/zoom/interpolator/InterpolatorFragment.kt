package com.dafay.demo.zoom.interpolator

import android.annotation.SuppressLint
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.util.TypedValue
import android.view.Choreographer
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseFragment
import com.dafay.demo.lib.base.utils.debug
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.FragmentInterpolatorBinding


/**
 * 演示常用插值器的运动轨迹
 */
class InterpolatorFragment : BaseFragment(R.layout.fragment_interpolator) {
    override val binding: FragmentInterpolatorBinding by viewBinding()

    private var curInterpolator: Interpolator = LinearInterpolator()

    // 动画持续时间，1 秒
    private var duration = 1000L

    // 持续时间倒数
    private var durationReciprocal = 0f

    // 动画开启的时间
    private var startTime: Long = 0

    private val choreographer = Choreographer.getInstance()

    override fun initViews() {
        super.initViews()
        durationReciprocal = 1 / duration.toFloat()
    }

    override fun bindListener() {
        super.bindListener()
        binding.btnStart.setOnClickListener {
            startAnim()
        }
        binding.mcvCard.setOnClickListener {
            showInterpolatorMenu(binding.tvName)
        }
    }

    private fun showInterpolatorMenu(v: View) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.interpolator_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            val menuItemTitle = menuItem.title.toString()
            binding.tvName.text = menuItemTitle
            curInterpolator = when (menuItemTitle) {
                "AccelerateDecelerateInterpolator" -> AccelerateDecelerateInterpolator()
                "AccelerateInterpolator" -> AccelerateInterpolator()
                "AnticipateInterpolator" -> AnticipateInterpolator()
                "AnticipateOvershootInterpolator" -> AnticipateOvershootInterpolator()
                else -> LinearInterpolator()
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    /**
     * 开启动画
     */
    private fun startAnim() {
        startTime = AnimationUtils.currentAnimationTimeMillis()
        binding.rgvRate.clearTrack()
        postNextFrame()
    }


    /**
     * 递归执行动画，直到时间走完
     */
    private fun postNextFrame() {
        val timePassed: Int = (AnimationUtils.currentAnimationTimeMillis() - startTime).toInt()
        if (timePassed > duration) {
            return
        }
        val output: Float = curInterpolator.getInterpolation(timePassed * durationReciprocal)
        debug("timePassed=${timePassed} output=${output}")
        binding.vMobile.translationY = 200.dp2px * output
        binding.rgvRate.addTrackPoint(timePassed / duration.toFloat(), output)
        choreographer.postFrameCallback {
            postNextFrame()
        }
    }
}