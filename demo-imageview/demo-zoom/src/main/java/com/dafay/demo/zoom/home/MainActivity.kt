package com.dafay.demo.zoom.home

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.ActivityMainBinding
import com.dafay.demo.zoom.host.HostActivity
import com.dafay.demo.zoom.interpolator.CircleLayoutFragment
import com.dafay.demo.zoom.interpolator.CoordinateSystem1Fragment
import com.dafay.demo.zoom.interpolator.CoordinateSystem2Fragment
import com.dafay.demo.zoom.interpolator.InterpolatorFragment
import com.dafay.demo.zoom.matrix.Matrix1Fragment
import com.dafay.demo.zoom.matrix.Matrix2Fragment
import com.dafay.demo.zoom.matrix.Matrix3Fragment
import com.dafay.demo.zoom.overscroller.OverScrollerTrackFragment
import com.dafay.demo.zoom.overscroller.TestOverScrollerViewFragment
import com.dafay.demo.zoom.scroller.ScrollerTrackFragment
import com.dafay.demo.zoom.scroller.ScrollingTextViewFragment
import com.dafay.demo.zoom.scroller.TestScrollerViewFragment
import com.example.demo.biz.base.widgets.GridMarginDecoration

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val binding: ActivityMainBinding by viewBinding()

    private val homeItemList = ArrayList<HomeItem>().apply {
        this.add(HomeItem("Canvas 参考坐标系 1", CoordinateSystem1Fragment::class.java))
        this.add(HomeItem("Canvas 参考坐标系 2", CoordinateSystem2Fragment::class.java))

        this.add(HomeItem("Interpolator 速率图", InterpolatorFragment::class.java))


        this.add(HomeItem("Scroller 示例", TestScrollerViewFragment::class.java))
        this.add(HomeItem("Scroller 运动特性", ScrollerTrackFragment::class.java))
        this.add(HomeItem("滚动文字", ScrollingTextViewFragment::class.java))

        this.add(HomeItem("OverScroller 示例", TestOverScrollerViewFragment::class.java))
        this.add(HomeItem("OverScroller 运动特性", OverScrollerTrackFragment::class.java))

        this.add(HomeItem("OverScroller 应用", CircleLayoutFragment::class.java))

        this.add(HomeItem("矩阵1", Matrix1Fragment::class.java))
        this.add(HomeItem("矩阵2", Matrix2Fragment::class.java))
        this.add(HomeItem("矩阵3", Matrix3Fragment::class.java))

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