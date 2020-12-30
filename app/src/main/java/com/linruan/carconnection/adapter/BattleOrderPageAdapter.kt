package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.PagerAdapter
import com.linruan.carconnection.AdapterListener
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.utils.Util
import com.linruan.carconnection.view.refreshlayout.ARecyclerViewHelper
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import java.lang.ref.SoftReference

class BattleOrderPageAdapter : PagerAdapter {

    private var STARTPAGE = 1
    private var mContext: SoftReference<Context?>? = null
    private var mRefreshContainer: ArrayList<MRefreshLayout?>? = arrayListOf()
    private var mCallBack:AdapterListener?=null

    constructor(context: Context,callback: AdapterListener?) : super() {
        mContext = SoftReference(context)
        this.mCallBack=callback
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var currentPage = STARTPAGE
        var view = LayoutInflater.from(mContext?.get())
                .inflate(R.layout.item_vp_orders_list, null)
        container.addView(view)
        var mAdapter = BattleOrderAdapter(mContext?.get(), position)
        mAdapter.setListener(mCallBack)
        var mRefreshLayout = view.findViewById<MRefreshLayout>(R.id.mRefreshLayout)
                ?.apply {
                    setLayoutManager(LinearLayoutManager(mContext?.get()))
                    addItemDecoration(SpaceItemDecoraton(0, Util.px2dp(10), 0, 0, RecyclerView.VERTICAL))
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
        if (mRefreshContainer?.size ?: 0 >= position)
            mRefreshContainer?.add(position, mRefreshLayout)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount() = 4
    private var mOnViewPageRefreshListener: OnViewPageRefreshListener? = null

    interface OnViewPageRefreshListener {
        fun onRefresh(mRefreshLayout: MRefreshLayout, adapter: BattleOrderAdapter, status: Int, currentPage: Int, isLoadMore: Boolean)
    }
    fun refresh(sort:Int=-1){
        if(sort==-1){
            mRefreshContainer?.forEach {
                it?.autoRefresh()
            }
        }else{
            if(mRefreshContainer?.size?:0>=sort){
                mRefreshContainer?.get(sort)?.autoRefresh()
            }
        }
    }

    fun setOnViewPageRefreshListener(onViewPageRefreshListener: OnViewPageRefreshListener) {
        this.mOnViewPageRefreshListener = onViewPageRefreshListener
    }
    fun destory(){
        mRefreshContainer?.clear()
    }
}
