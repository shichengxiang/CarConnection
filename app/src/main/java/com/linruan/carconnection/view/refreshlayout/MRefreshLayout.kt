package com.linruan.carconnection.view.refreshlayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.R
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener

class MRefreshLayout : RelativeLayout {
    private var refreshLayout: SmartRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var emptyView: View? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        var root = LayoutInflater.from(context).inflate(R.layout.layout_refresh, null)
        this.refreshLayout = root.findViewById(R.id.smartRefreshLayout)
        this.recyclerView = root.findViewById(R.id.recycler_view)
        this.emptyView = root.findViewById(R.id.emptyView)
        addView(root,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        if(this.recyclerView is RecyclerViewEmptySupport){
            (this.recyclerView as RecyclerViewEmptySupport)?.setEmptyView(this.emptyView)
        }
    }
    fun getRecyclerView()=this.recyclerView
    fun setOnRefreshListener(listener:OnRefreshListener){
        this.refreshLayout?.setOnRefreshListener(listener)
    }
    fun setOnLoadMoreListener(listener:OnLoadMoreListener){
        this.refreshLayout?.setOnLoadMoreListener(listener)
    }
    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager){
        this.recyclerView?.layoutManager=layoutManager
    }
    fun addItemDecoration(decoration:RecyclerView.ItemDecoration){
        this.recyclerView?.addItemDecoration(decoration)
    }
    fun setEmpty(isEmpty: Boolean){
        if(isEmpty){
            this.recyclerView?.visibility=View.GONE
            this.emptyView?.visibility=View.VISIBLE
        }else{
            this.recyclerView?.visibility=View.VISIBLE
            this.emptyView?.visibility=View.GONE
        }
    }
    fun setEmptyView(view:View){
        if(this.recyclerView is RecyclerViewEmptySupport){
            (this.recyclerView as RecyclerViewEmptySupport)?.setEmptyView(view)
        }
    }
    fun setEnableRefresh(enable:Boolean){
        this.refreshLayout?.setEnableRefresh(enable)
    }
    fun setEnableLoadMore(enable:Boolean){
        this.refreshLayout?.setEnableLoadMore(enable)
    }
    fun autoRefresh(){
        this.refreshLayout?.autoRefresh()
    }
    fun setAdapter(adapter:RecyclerView.Adapter<*>){
        this.recyclerView?.adapter=adapter
    }
    fun finishRefresh(){
        this.refreshLayout?.finishRefresh()
    }
    fun finishRefresh(delay:Int){
        this.refreshLayout?.finishRefresh(delay)
    }
    fun finishLoadMore(){
        this.refreshLayout?.finishLoadMore()
    }
    fun finishLoadMore(delay:Int){
        this.refreshLayout?.finishLoadMore(delay)
    }
    fun finishLoadMoreWithNoMoreData(){
        refreshLayout?.finishLoadMoreWithNoMoreData()
    }


}