package com.dafay.demo.zoom.home

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.ActivityMainBinding
import com.dafay.demo.zoom.host.HostActivity
import com.dafay.demo.zoom.interpolator.InterpolatorFragment
import com.dafay.demo.zoom.overscroller.OverScrollerTrackFragment
import com.dafay.demo.zoom.overscroller.TestOverScrollerViewFragment
import com.dafay.demo.zoom.scroller.ScrollerTrackFragment
import com.dafay.demo.zoom.scroller.TestScrollerViewFragment
import com.example.demo.biz.base.widgets.GridMarginDecoration

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val binding: ActivityMainBinding by viewBinding()

    private val homeItemList = ArrayList<HomeItem>().apply {
        this.add(HomeItem("Interpolator 速率图", InterpolatorFragment::class.java))
        this.add(HomeItem("Scroller 示例", TestScrollerViewFragment::class.java))
        this.add(HomeItem("Scroller 运动轨迹", ScrollerTrackFragment::class.java))

        this.add(HomeItem("OverScroller 示例", TestOverScrollerViewFragment::class.java))
        this.add(HomeItem("OverScroller 运动轨迹", OverScrollerTrackFragment::class.java))
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