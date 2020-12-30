package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.BBS
import com.linruan.carconnection.moudle.TalkPresenter
import com.linruan.carconnection.moudle.TalkView
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_message.view.*
import java.lang.ref.SoftReference

class BBSPageAdapter : PagerAdapter, TalkView {
    private var mContext: SoftReference<Context?>? = null
    private var arrayAdapters: ArrayList<TalkAdapter> = arrayListOf()
    private var arrayRefreshLayouts: ArrayList<MRefreshLayout> = arrayListOf()
    private var mPresenter: TalkPresenter? = null

    constructor(context: Context?) : super() {
        mContext = SoftReference(context)
        mPresenter = TalkPresenter(this)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view = LayoutInflater.from(mContext?.get())
                .inflate(R.layout.item_vp_orders_list, null)
        container.addView(view)
        var mAdapter = TalkAdapter(mContext?.get(), position)
        if (position <= arrayAdapters.size)
            arrayAdapters.add(position, mAdapter)
        var mRefreshLayout = view.findViewById<MRefreshLayout>(R.id.mRefreshLayout)
                ?.apply {
                    setLayoutManager(LinearLayoutManager(mContext?.get()))
                    addItemDecoration(DividerItemDecoration(
                        mContext?.get(),
                        LinearLayoutManager.VERTICAL
                                                           ).apply {
                        mContext?.get()
                                ?.let {
                                    setDrawable(
                                        ContextCompat.getDrawable(
                                            it.applicationContext,
                                            R.drawable.line_long
                                                                 )!!
                                               )
                                }
                    })
                    setAdapter(mAdapter!!)
                    setOnRefreshListener(OnRefreshListener { mPresenter?.getBbs(mRefreshLayout, position, false) })
                    setOnLoadMoreListener(OnLoadMoreListener { mPresenter?.getBbs(mRefreshLayout, position, true) })
                    autoRefresh()
                }
        mAdapter.setOnClickListener(object : OnViewClickListener {
            override fun onClick(view: View, position: String) {
                mPresenter?.setAttentino(view as TextView, position)
            }
        })
        if (position <= arrayRefreshLayouts.size)
            arrayRefreshLayouts.add(position, mRefreshLayout!!)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount() = 3

    override fun onGetBBSSuccess(list: ArrayList<BBS>?, isMore: Boolean, tag: Int) {
        if (list == null) {
            if (!isMore) {
                arrayRefreshLayouts[tag].setEmpty(true)
            }
            return
        }
        if(tag>=arrayRefreshLayouts.size){
            return
        }
        arrayRefreshLayouts[tag].setEmpty(false)
        if (isMore) {
            arrayAdapters[tag]?.addData(list)
        } else {
            arrayAdapters[tag]?.setData(list)
        }
    }

    /**
     * 刷新页面
     */
    fun refresh(index:Int=-1){
        if(index==-1){
            arrayRefreshLayouts?.forEach {
                it.autoRefresh()
            }
            return
        }
        if(arrayRefreshLayouts.size>index){
            arrayRefreshLayouts[index].autoRefresh()
        }
    }


    override fun onAttentionSuccess(followId: String, isFollow: Int) {
        arrayAdapters.forEach { adapter ->
            var data=adapter.getData()
            for (index in 0 until data.size){
                var bean= data[index]
                if (bean.user_id == followId) {
                    bean.user?.follow = isFollow
                    adapter.notifyItemChanged(index)
                }
            }
        }
        if(arrayRefreshLayouts.size>=3){
            arrayRefreshLayouts[2]?.autoRefresh()
        }

    }

    override fun onError(err: String?) {
    }

}
