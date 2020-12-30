package com.linruan.carconnection.ui.mine

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.linruan.carconnection.AdapterListener
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.GoodsOrderPageAdapter
import com.linruan.carconnection.adapter.StoreOrderAdapter
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.net.GetExpressResponse
import com.linruan.carconnection.moudle.GoodsOrderPresenter
import com.linruan.carconnection.moudle.GoodsOrderView
import com.linruan.carconnection.toast
import com.linruan.carconnection.utils.Util
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import kotlinx.android.synthetic.main.activity_storeorder.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StoreOrderActivity : BaseActivity(), GoodsOrderView {

    companion object {
        var ORDERTAG = "ORDERTAG_STORE"
        val TAG_FILLEXPRESS = 101//填写物流信息
    }

    private var mPresenter: GoodsOrderPresenter? = null
    private var mPageAdapter: GoodsOrderPageAdapter? = null
    private var mExpressArray= arrayListOf<String>()//获取的物流公司
    private var mAdapterListener = object : AdapterListener {
        override fun call(tag: Int, position: Int, vararg params: String) {
            when (tag) {
                TAG_FILLEXPRESS -> {
                    var orderId=params[0]
                    showExpressDialog(orderId)
                }
            }
        }
    }

    override fun getLayout() = R.layout.activity_storeorder

    override fun initView() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault()
                .register(this)
        mPresenter = GoodsOrderPresenter(this)
        toolbar.tvTitle.apply {
            text = "商城订单"
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        toolbar.ivBack.setImageResource(R.drawable.ic_back_white)
        tabLayout.addTab(
            tabLayout.newTab()
                .setText("全部订单")
        )
        tabLayout.addTab(
            tabLayout.newTab()
                .setText("待付款")
        )
        tabLayout.addTab(
            tabLayout.newTab()
                .setText("待发货")
        )
        tabLayout.addTab(
            tabLayout.newTab()
                .setText("待收货")
        )
        tabLayout.addTab(
            tabLayout.newTab()
                .setText("退货/售后")
        )
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                vpOrders.currentItem = tab?.position ?: 0
            }
        })
        var onViewPageListener = object : GoodsOrderPageAdapter.OnViewPageRefreshListener {
            override fun onRefresh(
                mRefreshLayout: MRefreshLayout,
                adapter: StoreOrderAdapter,
                status: Int,
                currentPage: Int,
                isLoadMore: Boolean
            ) {
                mPresenter?.getGoodsOrderList(
                    mRefreshLayout,
                    adapter,
                    status,
                    currentPage,
                    isLoadMore
                )
            }
        }
        mPageAdapter = GoodsOrderPageAdapter(this, mAdapterListener).apply {
            setOnViewPageRefreshListener(onViewPageListener)
        }
        vpOrders.adapter = mPageAdapter
        vpOrders.offscreenPageLimit = 2
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
        var tag = intent.getIntExtra(ORDERTAG, 0)
        tabLayout.selectTab(tabLayout.getTabAt(tag))
        //拉取物流数据
        mPresenter?.getExpress()
    }

    override fun onGetExpressSuccess(data: ArrayList<GetExpressResponse.ExpressBean>?) {
        if(data.isNullOrEmpty()){
            return
        }
        mExpressArray.clear()
        data.forEach {
            mExpressArray.add(it.name?:"")
        }
    }

    override fun onPushExpressSuccess() {
        mPageAdapter?.refresh(vpOrders.currentItem)
    }

    /**
     * 填写物流信息
     */
    private fun showExpressDialog(orderId: String) {
        if(mExpressArray.size==0){
            toast("物流信息获取失败")
            return
        }
        var companies = mExpressArray.toTypedArray()
        var currentCompany = companies[0]
        var dialog = object : CommonDialog(this, R.layout.dialog_fill_express) {
            override fun initView(root: View) {
                window?.attributes?.width = (Util.getScreenWidth() * 0.8f).toInt()
                var spinner = root.findViewById<Spinner>(R.id.tvCompany)
                var arrayAdapter = ArrayAdapter<String>(
                    this@StoreOrderActivity, R.layout.simple_spinner_text,
                    companies
                )
                spinner.adapter = arrayAdapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        currentCompany = companies[position]
                    }

                }
                var etNumber = root.findViewById<EditText>(R.id.etNumber)
                root.findViewById<View>(R.id.tvCancel).setOnClickListener { dismiss() }
                root.findViewById<View>(R.id.tvOk).setOnClickListener {
                    var number = etNumber.text.toString()
                    if (number.isNullOrBlank()) {
                        toast("请填写正确单号")
                        return@setOnClickListener
                    }
                    dismiss()
                    mPresenter?.pushExpress(currentCompany,number,orderId)
                }
            }
        }
        dialog.show()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReloadData(messageEvent: MessageEvent) {
        if (messageEvent.code == MessageEvent.REFRESH_STOREORDERACTIVITY) {
            if (messageEvent.message.isNullOrEmpty()) {
                mPageAdapter?.refresh(vpOrders.currentItem)
            } else {
                mPageAdapter?.refresh(messageEvent.message[0].toInt())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
        EventBus.getDefault()
            .unregister(this)
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
