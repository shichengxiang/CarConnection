package com.linruan.carconnection.view.refreshlayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewEmptySupport : RecyclerView {
    /**
     * 当数据为空时展示的View
     */
    private var mEmptyView: View? = null

    /**
     * 创建一个观察者
     * *为什么要在onChanged里面写？
     * * 因为每次notifyDataChanged的时候，系统都会调用这个观察者的onChange函数
     * * 我们大可以在这个观察者这里判断我们的逻辑，就是显示隐藏
     */
    private val emptyObserver: AdapterDataObserver = object : AdapterDataObserver() {
        @SuppressLint("LongLogTag")
        override fun onChanged() {
            val adapter = adapter //这种写发跟之前我们之前看到的ListView的是一样的，判断数据为空否，再进行显示或者隐藏
            if (adapter != null && mEmptyView != null) {
                if (adapter.itemCount == 0) {
                    mEmptyView?.visibility = View.VISIBLE
                    this@RecyclerViewEmptySupport.visibility = View.GONE
                } else {
                    mEmptyView?.visibility = View.GONE
                    this@RecyclerViewEmptySupport.visibility = View.VISIBLE
                }
            }
        }
    }

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {}

    /**
     * * @param emptyView 展示的空view
     */
    fun setEmptyView(emptyView: View?) {
        mEmptyView = emptyView
    }

    @SuppressLint("LongLogTag")
    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(emptyObserver)
        //当setAdapter的时候也调一次（实际上，经我粗略验证，不添加貌似也可以。不行就给添上呗，多大事嘛）
        emptyObserver.onChanged()
    }

    companion object {
        private const val TAG = "RecyclerViewEmptySupport"
    }
}