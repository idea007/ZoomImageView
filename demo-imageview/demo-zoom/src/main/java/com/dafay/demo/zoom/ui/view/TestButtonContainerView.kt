package com.dafay.demo.zoom.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.databinding.LayoutTestButtonContainerBinding
import com.google.android.material.button.MaterialButton


/**
 * 一个测试按钮的容器视图，方便快速添加一些列按钮
 */
class TestButtonContainerView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var _binding: LayoutTestButtonContainerBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = LayoutTestButtonContainerBinding.inflate(LayoutInflater.from(context), this, true)
        initViews()
    }

    private fun initViews() {

    }

    /**
     * 添加一个按钮
     */
    fun addButton(text: String?, func: (() -> Unit)? = null) {
        val btn = MaterialButton(context)
        val marginLayoutParams =
            MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
        marginLayoutParams.marginStart = 4.dp2px
        marginLayoutParams.marginEnd = 4.dp2px
        btn.text = text ?: "null"
        btn.setOnClickListener {
            func?.invoke()
        }
        binding.llContainer.addView(btn, marginLayoutParams)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

}