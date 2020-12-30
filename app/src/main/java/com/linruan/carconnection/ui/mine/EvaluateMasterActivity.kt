package com.linruan.carconnection.ui.mine

import android.os.Handler
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.AdvantageAdapter
import com.linruan.carconnection.decorview.GridSpaceItemDecoraton
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.entities.net.GetPingJiaWorkerResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.toast
import com.linruan.carconnection.utils.Util
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_evaluatemaster.*

/**
 * author：shichengxiang on 2020/6/24 15:45
 * email：1328911009@qq.com
 */
class EvaluateMasterActivity : BaseActivity() {
    private var mCurrentStar = 5
    private var mAdvantageAdapter: AdvantageAdapter? = null
    override fun getLayout() = R.layout.activity_evaluatemaster

    override fun initView() {
        toolbar.setTitle("评价")
        var repairId = intent.getStringExtra("repairId") ?: ""
        var orderNo = intent.getStringExtra("orderNo") ?: ""
        tvOrderNo.text = "订单编号：$orderNo"
        handleStar()
        rvAdvantages.layoutManager = GridLayoutManager(this, 2)
        rvAdvantages.addItemDecoration(
            GridSpaceItemDecoraton(
                Util.px2dp(10),
                Util.px2dp(10),
                0,
                0,
                2
            )
        )
        mAdvantageAdapter = AdvantageAdapter(this)
        rvAdvantages.adapter = mAdvantageAdapter
        btnCommit.setOnClickListener {
            var content = etSaySomething.text.toString()
            if (content.isBlank()) {
                toast("请填写评价内容")
                return@setOnClickListener
            }
            var titles = mAdvantageAdapter?.getSelectedAll() ?: ""
            Client.getInstance().releaeCommentOfRepair(
                repairId,
                titles,
                content,
                mCurrentStar.toString(),
                object : JsonCallback<BaseResponse>() {
                    override fun onSuccess(response: Response<BaseResponse?>?) {
                        if (response?.body().isSuccess()) {
                            toast("评价成功")
                            Handler().postDelayed({
                                finish()
                            }, 1500)
                        } else {
                            toast(response?.body()?.msg ?: "")
                        }
                    }
                })
        }
        getPingJiaList()
    }

    /**
     * 获取师傅评价标题
     */
    private fun getPingJiaList() {
        Client.getInstance().getPingJiaOfWorker(object : JsonCallback<GetPingJiaWorkerResponse>() {
            override fun onSuccess(response: Response<GetPingJiaWorkerResponse?>?) {
                var body = response?.body()
                if (body.isSuccess()) {
                    mAdvantageAdapter?.setData(body?.data?.list)
                } else {
                    toast(body?.msg ?: "获取评价列表失败")
                }
            }
        })
    }

    private fun handleStar() {
        var count = mStarContainer.childCount
        for (index in 0 until count) {
            var child = mStarContainer.getChildAt(index)
            child.setOnClickListener {
                mCurrentStar = index + 1
                for (step in 0 until index + 1) {
                    mStarContainer.getChildAt(step).isSelected = true
                }
                for (step in index + 1 until count) {
                    mStarContainer.getChildAt(step).isSelected = false
                }
            }
            if (index == count - 1) {
                //默认五星
                child.performClick()
            }
        }
    }
}
