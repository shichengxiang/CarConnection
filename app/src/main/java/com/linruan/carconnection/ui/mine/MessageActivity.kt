package com.linruan.carconnection.ui.mine

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class MessageActivity : BaseActivity(), MessageView {
    private var mPresenter:MessagePresenter?= null

    override fun getLayout() = R.layout.activity_message

    override fun initView() {
        mPresenter= MessagePresenter(this)
        toolbar.getLeftImageView().setImageResource(R.drawable.ic_back_black)
        toolbar.tvTitle.apply {
            setTextColor(Color.parseColor("#FF232323"))
            text = "消息通知"
        }
        var adapter = MsgAdapter(this.applicationContext) //装饰adapter
        mRefreshLayout.apply {
            setLayoutManager(LinearLayoutManager(this@MessageActivity))
            addItemDecoration(SpaceItemDecoraton(0,Util.px2dp(10),0,0,RecyclerView.VERTICAL))
            mRefreshLayout.setAdapter(adapter)
            setOnRefreshListener(OnRefreshListener { mPresenter?.getMessgaeList(1,false,adapter,mRefreshLayout) })
            setOnLoadMoreListener(OnLoadMoreListener { mPresenter?.getMessgaeList(1,true,adapter,mRefreshLayout) })
            autoRefresh()
        }
    }


    override fun onError(err: String?) {
        toast(err?:"")
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }
}