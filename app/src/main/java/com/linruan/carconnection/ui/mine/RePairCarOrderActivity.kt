package com.linruan.carconnection.ui.mine

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.RepairCarOrderAdapter
import com.linruan.carconnection.adapter.RepairOrderPageAdapter
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.moudle.RepairCarOrderPresenter
import com.linruan.carconnection.moudle.RepairCarOrderView
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import kotlinx.android.synthetic.main.activity_repaircarorder.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RePairCarOrderActivity : BaseActivity(), RepairCarOrderView {

    companion object {
        var ORDERTAG = "ORDERTAG_CAR"
        const val RESULTCODE_SWITCHTAB = 1001
    }

    private var mPresenter: RepairCarOrderPresenter? = null
    private var mPageAdapter: RepairOrderPageAdapter? = null
    override fun getLayout() = R.layout.activity_repaircarorder

    override fun initView() {
        mPresenter = RepairCarOrderPresenter(this)
        toolbar.tvTitle.apply {
            text = "修车订单"
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        toolbar.ivBack.setImageResource(R.drawable.ic_back_white)
        tabLayout.apply {
            addTab(newTab().setText("全部"))
            addTab(newTab().setText("待付款"))
            addTab(newTab().setText("进行中"))
            addTab(newTab().setText("已完成"))
            addTab(newTab().setText("已取消"))
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                vpRepairOrders.currentItem = tab?.position ?: 0
            }
        })
        var onViewPageListener = object : RepairOrderPageAdapter.OnViewPageRefreshListener {
            override fun onRefresh(
                mRefreshLayout: MRefreshLayout,
                adapter: RepairCarOrderAdapter,
                status: Int,
                currentPage: Int,
                isLoadMore: Boolean
            ) {
                mPresenter?.getRepairCarOrderList(
                    mRefreshLayout,
                    adapter,
                    status,
                    currentPage,
                    isLoadMore
                )
            }
        }
        mPageAdapter = RepairOrderPageAdapter(this)
        mPageAdapter?.setOnViewPageRefreshListener(onViewPageListener)
        vpRepairOrders.adapter = mPageAdapter
        vpRepairOrders.offscreenPageLimit = 5
        vpRepairOrders.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }

        })
        var tag = intent.getIntExtra(ORDERTAG, 0)
        tabLayout.selectTab(tabLayout.getTabAt(tag))
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshRepairOrderList(event: MessageEvent) {
        if (event.code == MessageEvent.REFRESH_REPAIRORDERACTIVITY) {
            if (event.message.isNullOrEmpty()) {
                mPageAdapter?.refresh()
            } else {
                var index = event.message[0].toInt()
                tabLayout.selectTab(tabLayout.getTabAt(index))
                mPageAdapter?.refresh(index)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPageAdapter?.refresh()
    }
}
