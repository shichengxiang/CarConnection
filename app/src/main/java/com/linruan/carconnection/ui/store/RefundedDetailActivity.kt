package com.linruan.carconnection.ui.store

import androidx.recyclerview.widget.LinearLayoutManager
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.InnerGoodsAdapter
import com.linruan.carconnection.entities.GoodsOrder
import com.linruan.carconnection.moudle.ApplyRefundPresenter
import com.linruan.carconnection.moudle.ApplyRefundView
import com.linruan.carconnection.toast
import com.linruan.carconnection.utils.Util
import com.yanzhenjie.album.widget.photoview.DefaultOnDoubleTapListener
import kotlinx.android.synthetic.main.activity_refundeddetail.*
import java.math.BigDecimal

/**
 * author：shichengxiang on 2020/6/5 10:44
 * email：1328911009@qq.com
 */
class RefundedDetailActivity : BaseActivity(), ApplyRefundView {

    companion object {
        @JvmField
        val ORDERID_KEY = "orderId"
    }

    private var mPresenter: ApplyRefundPresenter? = null
    private var mOrderId = ""
    override fun getLayout() = R.layout.activity_refundeddetail

    override fun initView() {
        mPresenter = ApplyRefundPresenter(this)
        mOrderId = intent.getStringExtra(ORDERID_KEY) ?: ""
        toolbar.setTitle("退款详情")
        rvGoods.layoutManager = LinearLayoutManager(this)
        mPresenter?.getOrderDetail(mOrderId)
    }

    override fun onApllyRefundSuccess() {

    }

    override fun onGetGoodsOrderSuccess(detail: GoodsOrder?) {
        if (detail == null) return
        var refundBean = detail.refund
        tvRefundAmount.text = Util.moneyformat(BigDecimal(detail.refund?.total ?: "0"))
        var goodss = detail.ordersGoods
        rvGoods.adapter = InnerGoodsAdapter(
            this,
            GoodsOrder.STATE_ALL,
            goodss
        )
        tvRefundReason.text = "退款原因：${refundBean?.explain ?: ""}"
        tvAmount.text = "退款金额：${refundBean?.total ?: ""}"
        tvTime.text = "申请时间：${Util.getDataToString(refundBean?.create_time, "yyyy-MM-dd HH:mm:ss")}"
        tvTopTime.text = Util.getDataToString(refundBean?.create_time, "yyyy-MM-dd HH:mm:ss")
        tvNo.text = "退款编号：${refundBean?.orderno_t}"
        if (detail.step == GoodsOrder.STEP_RETURNING) {
            if (detail.refund?.status == GoodsOrder.REFUND_STATUS_FAIL) {
                tvState.text = "退款失败"
            } else {
                tvState.text = "退款中"
            }
        } else {
            tvState.text = "已退款"
        }
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
