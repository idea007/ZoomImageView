package com.dafay.demo.zoom.ui.page.home

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.ActivityMainBinding
import com.dafay.demo.zoom.ui.page.zoom.ZoomImageViewExampleFragment
import com.dafay.demo.zoom.ui.page.host.HostActivity
import com.dafay.demo.zoom.ui.page.interpolator.CircleLayoutFragment
import com.dafay.demo.zoom.ui.page.coord.CoordinateSystem3Fragment
import com.dafay.demo.zoom.ui.page.interpolator.InterpolatorFragment
import com.dafay.demo.zoom.ui.page.matrix.Matrix2Fragment
import com.dafay.demo.zoom.ui.page.matrix.Matrix3Fragment
import com.dafay.demo.zoom.ui.page.overscroller.OverScrollerTrackFragment
import com.dafay.demo.zoom.ui.page.overscroller.TestOverScrollerViewFragment
import com.dafay.demo.zoom.ui.page.scroller.ScrollerTrackFragment
import com.dafay.demo.zoom.ui.page.scroller.ScrollingTextViewFragment
import com.dafay.demo.zoom.ui.page.scroller.TestScrollerViewFragment
import com.dafay.demo.zoom.ui.page.zoom.ZoomImageViewFullScreenFragment
import com.example.demo.biz.base.widgets.GridMarginDecoration

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val binding: ActivityMainBinding by viewBinding()

    private val homeItemList = ArrayList<HomeItem>().apply {
        this.add(HomeItem("Canvas 参考坐标系", CoordinateSystem3Fragment::class.java))
        this.add(HomeItem("Interpolator 速率图", InterpolatorFragment::class.java))

        this.add(HomeItem("Scroller 示例", TestScrollerViewFragment::class.java))
        this.add(HomeItem("Scroller 运动特性", ScrollerTrackFragment::class.java))
        this.add(HomeItem("滚动文字", ScrollingTextViewFragment::class.java))
        this.add(HomeItem("OverScroller 示例", TestOverScrollerViewFragment::class.java))
        this.add(HomeItem("OverScroller 运动特性", OverScrollerTrackFragment::class.java))
        this.add(HomeItem("手势应用", CircleLayoutFragment::class.java))
        this.add(HomeItem("矩阵 2", Matrix2Fragment::class.java))
        this.add(HomeItem("矩阵 3", Matrix3Fragment::class.java))
        this.add(HomeItem("ZoomImageView 逐步实现", ZoomImageViewExampleFragment::class.java))
        this.add(HomeItem("ZoomImageView 预览", ZoomImageViewFullScreenFragment::class.java))
    }

    private lateinit var homeAdapter: HomeAdapter

    override fun initViews() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        homeAdapter = HomeAdapter()
        binding.rvRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.rvRecyclerview.adapter = homeAdapter
        binding.rvRecyclerview.addItemDecoration(GridMarginDecoration(8.dp2px, 4.dp2px, 8.dp2px, 4.dp2px))
        homeAdapter.onItemClickListener = object : HomeAdapter.HomeViewHolder.OnItemClickListener {
            override fun onClickItem(view: View, position: Int, homeItem: HomeItem) {
                if (homeItem.clazz != null) {
                    HostActivity.startActivity(this@MainActivity, homeItem.clazz)
                }
            }
        }
    }

    override fun initializeData() {
        homeAdapter.setDatas(homeItemList)
    }
}