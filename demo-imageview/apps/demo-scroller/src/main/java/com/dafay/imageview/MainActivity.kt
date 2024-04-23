package com.dafay.imageview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var customScrollView: CustomScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customScrollView = findViewById(R.id.sv_scrollview)

        

        // 在某个事件触发时进行平滑滚动
        customScrollView?.smoothScrollTo(0, 500); // 滚动到Y轴500的位置
    }
}