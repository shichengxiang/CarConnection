package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.linruan.carconnection.AdapterListener
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.GoodsOrder
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import java.lang.ref.SoftReference

class GoodsOrderPageAdapter : PagerAdapter {
    private var STARTPAGE = 1
    private var mContext: SoftReference<Context?>? = null
    private var refreshContainer: ArrayList<MRefreshLayout?>? = null
    private var mCallback:AdapterListener?=null
    //    private var mAdapter: StoreOrderAdapter? = null
    //    private var mTestData = arrayListOf(
    //        GoodsOrder().apply { state = GoodsOrder.STATE_WAITPAY },
    //        GoodsOrder().apply { state = GoodsOrder.STATE_WAITEXPRESS },
    //        GoodsOrder().apply { state = GoodsOrder.STATE_WAITRECEIVE },
    //        GoodsOrder().apply { state = GoodsOrder.STATE_WAITAFTERSALE },
    //        GoodsOrder().apply { state = GoodsOrder.STATE_RETURNING },
    //        GoodsOrder().apply { state = GoodsOrder.STATE_RETURNED })

    constructor(context: Context,callback:AdapterListener) : super() {
        mContext = SoftReference(context)
        refreshContainer = arrayListOf()
        this.mCallback=callback
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var currentPage = STARTPAGE
        var view = LayoutInflater.from(mContext?.get())
                .inflate(R.layout.item_vp_orders_list, null)
        container.addView(view)
        var mAdapter = StoreOrderAdapter(mContext?.get(), position,mCallback)
        var mRefreshLayout = view.findViewById<MRefreshLayout>(R.id.mRefreshLayout)
                ?.apply {
                    setLayoutManager(LinearLayoutManager(mContext?.get()))
                    setAdapter(mAdapter!!)
                    setOnRefreshListener(OnRefreshListener {
                        currentPage = STARTPAGE
                        mOnViewPageRefreshListener?.onRefresh(this, mAdapter!!, position, currentPage, false)
                    })
                    setOnLoadMoreListener(OnLoadMoreListener {
                        currentPage++
                        mOnViewPageRefreshListener?.onRefresh(this, mAdapter!!, position, currentPage, true)
                    })
                    autoRefresh()
                }
        if (refreshContainer?.size ?: 0 >= position)
            refreshContainer?.add(position, mRefreshLayout)
        return view
    }
    fun refresh(position: Int){
        if(refreshContainer?.size?:0>position){
            refreshContainer!![position]?.autoRefresh()
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount() = 5
    private var mOnViewPageRefreshListener: OnViewPageRefreshListener? = null

    interface OnViewPageRefreshListener {
        fun onRefresh(mRefreshLayout: MRefreshLayout, adapter: StoreOrderAdapter, status: Int, currentPage: Int, isLoadMore: Boolean)
    }

    fun setOnViewPageRefreshListener(onViewPageRefreshListener: OnViewPageRefreshListener) {
        this.mOnViewPageRefreshListener = onViewPageRefreshListener
    }

}
