package com.linruan.carconnection.ui.master

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.MsgAdapter
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.moudle.MessagePresenter
import com.linruan.carconnection.moudle.MessageView
import com.linruan.carconnection.toast
import com.linruan.carconnection.utils.Util
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_battlenews.*

/**
 * author：shichengxiang on 2020/6/3 09:43
 * email：1328911009@qq.com
 */
class BattleNewsActivity : BaseActivity(), MessageView {
    private var mPresenter: MessagePresenter? = null
    private var mNewsAdapter: MsgAdapter? = null
    private var mCurrentIndex = 1 //默认系统消息  2 推送消息
    override fun getLayout() = R.layout.activity_battlenews

    override fun initView() {
        mPresenter = MessagePresenter(this)
        toolbar.setTitle("消息通知")
        tabLayout.apply {
            addTab(newTab().setText("系统消息"))
            addTab(newTab().setText("抢单消息"))
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                mCurrentIndex = (tab?.position ?: 1) + 1
                mNewsAdapter?.clear()
                mRefreshLayout?.autoRefresh()
            }

        })
        mNewsAdapter = MsgAdapter(this)
        mRefreshLayout.apply {
            setLayoutManager(LinearLayoutManager(this@BattleNewsActivity))
            addItemDecoration(SpaceItemDecoraton(0, Util.px2dp(10), 0, 0, RecyclerView.VERTICAL))
            setAdapter(mNewsAdapter!!)
            setOnRefreshListener(OnRefreshListener { mPresenter?.getMessgaeList(mCurrentIndex,false,mNewsAdapter,mRefreshLayout) })
            setOnLoadMoreListener(OnLoadMoreListener { mPresenter?.getMessgaeList(mCurrentIndex,true,mNewsAdapter,mRefreshLayout)  })
            autoRefresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}