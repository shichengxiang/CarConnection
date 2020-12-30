package com.linruan.carconnection.ui.store

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.EvaluateGoodsAfterAdapter
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.activity_evaluatelist.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

/**
 * author：shichengxiang on 2020/6/15 16:55
 * email：1328911009@qq.com
 */
class EvaluateListActivity : BaseActivity() {
    private var mCurrentOrderNo = ""

    companion object {
        const val ORDERNO_KEY = "orderNo"
        const val ORDERID_KEY="orderId"
        const val GOODSLIST_KEY = "goodslist"
    }

    override fun getLayout() = R.layout.activity_evaluatelist

    override fun initView() {
        toolbar.getLeftImageView()
                .setImageResource(R.drawable.ic_back_white)
        toolbar.tvTitle.apply {
            text = "评价晒单"
            setTextColor(ContextCompat.getColor(this@EvaluateListActivity, R.color.white))
        }
        rvEvaluateList.layoutManager = LinearLayoutManager(this)
        rvEvaluateList.addItemDecoration(SpaceItemDecoraton(0, Util.px2dp(10), 0, 0, RecyclerView.VERTICAL, true))
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            mCurrentOrderNo = bundle.getString(ORDERNO_KEY) ?: ""
            var orderId=bundle.getString(ORDERID_KEY)?:""
            var list = bundle.getSerializable(GOODSLIST_KEY) as ArrayList<Goods>
            rvEvaluateList.adapter = EvaluateGoodsAfterAdapter(this, list,mCurrentOrderNo,orderId)
        }

    }
}