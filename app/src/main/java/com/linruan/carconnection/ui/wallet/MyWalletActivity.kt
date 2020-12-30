package com.linruan.carconnection.ui.wallet

import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.RecordAdapter
import com.linruan.carconnection.moudle.presenter.WalletPresenter
import com.linruan.carconnection.moudle.views.WalletView
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_mywallet.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class MyWalletActivity : BaseActivity(), WalletView {

    private var mAdapter: RecordAdapter? = null
    private var mPresenter: WalletPresenter? = null
    override fun getLayout() = R.layout.activity_mywallet

    override fun initView() {
        mPresenter = WalletPresenter(this)
        toolbar.ivBack.setImageResource(R.drawable.ic_back_white)
        toolbar.tvTitle.apply {
            text = "钱包"
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        tvAmount.text=UserManager.getUser()?.balance?:""
        tvGotoRecharge.setOnClickListener { Router.openUI(this, RechargeActivity::class.java) }
        mAdapter = RecordAdapter(this)
        mPresenter?.handleTabs(TabAllRecord, tabOrderRecord, tabShopRecord,mAdapter,mRefreshLayout)
        mRefreshLayout.apply {
            setLayoutManager(LinearLayoutManager(this@MyWalletActivity))
            setAdapter(mAdapter!!)
//            setOnRefreshListener(OnRefreshListener {
//                mPresenter?.getConsumeRecord(mAdapter,mRefreshLayout,false)
//            })
            setOnLoadMoreListener(OnLoadMoreListener {
                mPresenter?.getConsumeRecord(mAdapter,mRefreshLayout,true)
            })
//            autoRefresh()
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.getBalance()
    }
    override fun onGetBalanceSuccess() {
        tvAmount?.text=UserManager.getUser()?.balance?:""
    }
    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }

    override fun onError(err: String?) {
        toast(err?:"")
    }


}
