package com.linruan.carconnection.ui.master

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.amap.api.location.AMapLocation
import com.google.android.material.tabs.TabLayout
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.BattleOrderAdapter
import com.linruan.carconnection.adapter.BattleOrderPageAdapter
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.moudle.BattlePresenter
import com.linruan.carconnection.moudle.BattleView
import com.linruan.carconnection.ui.home.HomeFragment
import com.linruan.carconnection.utils.MapUtil
import com.linruan.carconnection.utils.Util
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import kotlinx.android.synthetic.main.fragment_battle.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

/**
 * author：shichengxiang on 2020/6/1 14:58
 * email：1328911009@qq.com
 */
class BattleFragment : BaseFragment(), BattleView {

    private var mPresenter: BattlePresenter? = null
    private var mPageAdapter: BattleOrderPageAdapter? = null

    companion object {
        const val REQUESTCODE_REFRESH = 1002
        const val REQUESTCODE_ISRECEIVED = 1003
    }

    override fun getLayout() = R.layout.fragment_battle

    override fun initView(view: View, savedInstanceState: Bundle?) {
        tabLayout.apply {
            addTab(newTab().setText("抢单"))
            addTab(newTab().setText("进行中"))
            addTab(newTab().setText("维修中"))
            addTab(newTab().setText("已完成"))
        }
        mPresenter = BattlePresenter(this)
        //        mOrderAdapter = activity?.let { BattleOrderAdapter(it, 0) }
        //        mRefreshLayout.apply {
        //            setLayoutManager(LinearLayoutManager(activity))
        //            setAdapter(mOrderAdapter!!)
        //            setOnRefreshListener(OnRefreshListener { finishRefresh() })
        //            setOnLoadMoreListener(OnLoadMoreListener { finishLoadMore(1000) })
        //        }
        ivGoBattleSetting.setOnClickListener {
            Router.openUI(
                activity,
                BattleSettingActivity::class.java
            )
        }
        ivGoNews.setOnClickListener { Router.openUI(activity, BattleNewsActivity::class.java) }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                vpOrders.currentItem = tab?.position ?: 0
            }
        })
        vpOrders.offscreenPageLimit = 4
        mPageAdapter = BattleOrderPageAdapter(activity!!, adapterListenr).apply {
            setOnViewPageRefreshListener(object : BattleOrderPageAdapter.OnViewPageRefreshListener {
                override fun onRefresh(
                    mRefreshLayout: MRefreshLayout,
                    adapter: BattleOrderAdapter,
                    status: Int,
                    currentPage: Int,
                    isLoadMore: Boolean
                ) {
                    mPresenter?.getRepairOrderOfMaster(
                        mRefreshLayout,
                        adapter,
                        status,
                        currentPage,
                        isLoadMore
                    )
                }
            })
        }
        vpOrders.adapter = mPageAdapter
        vpOrders.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
        /**
         * 获取经纬度
         */
        MapUtil.getInfoLocation(activity, object : MCallBack<AMapLocation> {
            override fun call(t: AMapLocation, position: Int) {
                UserManager.currentLocation = arrayOf(t.latitude, t.longitude)
            }

        })
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onResume() {
        super.onResume()
        mPageAdapter?.refresh()
        mPresenter?.getMasterInfo()
    }

    override fun getData() {
    }

    private var adapterListenr = object : AdapterListener {
        override fun call(tag: Int, position: Int, vararg params: String) {
            if (tag == BattleOrderAdapter.TAG_DELETEORDER) {//删除订单
                TipDialog(activity!!).apply {
                    getTitleView()?.text="提示"
                    getDescView()?.text="是否确定删除该订单？"
                    getLeftView()?.apply {
                        text="否"
                        setOnClickListener { dismiss() }
                    }
                    getRightView()?.apply {
                        text="是"
                        setOnClickListener { dismiss()
                        mPresenter?.deleteRepairOrder(params[0])}
                    }
                }.show()
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeBattleFragmentTab(event: MessageEvent) {
        if (event.code == MessageEvent.REFRESH_BATTLEFRAGMENT) {
            if (event.message.isNullOrEmpty()) {
                mPageAdapter?.refresh()
            } else {
                var index = event.message[0].toInt()
                tabLayout.selectTab(tabLayout.getTabAt(index))
                mPageAdapter?.refresh(index)
            }
        }
    }

    override fun onError(err: String?) {
        activity?.toast(err ?: "")
    }

    override fun onDeleteRepairOrderSuccess() {
        activity?.toast("删除订单成功")
        mPageAdapter?.refresh(4)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE_REFRESH) {
            var success = data?.extras?.getBoolean("isSuccess")
            if (success == true) {
                tabLayout.selectTab(tabLayout.getTabAt(1))
                mPageAdapter?.refresh()
            }
        } else if (requestCode == REQUESTCODE_ISRECEIVED && resultCode == REQUESTCODE_ISRECEIVED) {
            tabLayout.selectTab(tabLayout.getTabAt(2))
            mPageAdapter?.refresh()
        }
    }
}
