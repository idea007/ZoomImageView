package com.dafay.demo.zoom

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dafay.demo.lib.base.ui.base.BaseActivity
import com.dafay.demo.lib.base.utils.dp2px
import com.dafay.demo.zoom.R
import com.dafay.demo.zoom.databinding.ActivityMainBinding
import com.dafay.demo.zoom.scroller.TestScrollerViewFragment
import com.example.demo.biz.base.widgets.GridMarginDecoration

class MainActivity : BaseActivity(R.layout.activity_main) {
    override val binding: ActivityMainBinding by viewBinding()

    private val homeItemList = ArrayList<HomeItem>().apply {
        this.add(HomeItem("测试 Scroller", TestScrollerViewFragment::class.java))
        this.add(HomeItem("测试 Scroller 运动轨迹"))

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