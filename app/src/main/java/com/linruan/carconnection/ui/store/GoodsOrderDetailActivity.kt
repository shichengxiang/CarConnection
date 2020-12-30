package com.linruan.carconnection.ui.store

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.InnerGoodsAdapter
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.dialog.PayDialog
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.GoodsOrder
import com.linruan.carconnection.entities.net.GetOrderDetailResponse
import com.linruan.carconnection.listener.OnPayResultListener
import com.linruan.carconnection.moudle.GoodOrderDetailView
import com.linruan.carconnection.moudle.GoodsOrderDetailPresenter
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.mine.StoreOrderActivity
import com.linruan.carconnection.utils.Util
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_goodsorderdetail.*
import java.math.BigDecimal

class GoodsOrderDetailActivity : BaseActivity(), GoodOrderDetailView {
    companion object {
        const val ORDERIDKEY = "orderId"
    }

    private var mOrderId = ""
    private var mOrder: GoodsOrder? = null
    private var mPresenter: GoodsOrderDetailPresenter? = null

    override fun getLayout() = R.layout.activity_goodsorderdetail

    override fun initView() {
        if (intent.hasExtra(ORDERIDKEY))
            mOrderId = intent.getStringExtra(ORDERIDKEY)
        toolbar.setTitle("订单详情")
        mPresenter = GoodsOrderDetailPresenter(this)
        //        llAddressLayout.setOnClickListener { Router.openUI(this, AddressManagerActivity::class.java) }
        //        tvRefund.setOnClickListener { Router.openUI(this, RefundApplyActivity::class.java) }
        rvGoods.layoutManager = LinearLayoutManager(this)
        //        rvGoods.adapter= InnerGoodsAdapter(this,)
        getOrderDetail(mOrderId)
    }

    private fun getOrderDetail(orderId: String) {
        LoadingDialog.loading(this)
        Client.getInstance()
            .getGoodsOrderDetail(orderId, object : JsonCallback<GetOrderDetailResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<GetOrderDetailResponse?>?) {
                    var body = response?.body()
                    if (body?.isSuccess() == true) {
                        mOrder = body.data
                        handleData()
                    } else {
                        toast(body?.msg ?: "订单获取失败")
                    }
                }
            })
    }

    private fun handleData() {
        if (mOrder == null) return
        tvName.text = "${mOrder!!.name}   ${mOrder!!.phone}"
        //        tvTime.text="下单时间： ${DateUtils.forma}"
        tvArea.text = "${mOrder!!.address}"
        tvOrderNo.text = "订单编号：${mOrder!!.orderno}"
        tvMoney.text = "¥ ${BigDecimal(mOrder!!.price).setScale(2, BigDecimal.ROUND_DOWN)}"
        rvGoods.adapter = InnerGoodsAdapter(
            this@GoodsOrderDetailActivity,
            mOrder?.status ?: 0,
            mOrder?.ordersGoods
        )
        initButtons()
    }

    private fun initButtons() {
        if (mOrder == null) return
        var status = mOrder!!.status
        var step = mOrder!!.step
        when (status) {
            GoodsOrder.STATE_WAITPAY -> {
                tvDeleteOrder.visibility = View.VISIBLE
                tvGoPay.visibility = View.VISIBLE
            }
            GoodsOrder.STATE_WAITEXPRESS -> {
                tvRemindSend.visibility = View.VISIBLE
                tvCancelOrder.visibility = View.VISIBLE
            }
            GoodsOrder.STATE_WAITRECEIVE -> {
                tvConnectService.visibility = View.VISIBLE
                tvConfirmReceived.visibility = View.VISIBLE
            }
            GoodsOrder.STATE_WAITAFTERSALE -> {
                when (step) {
                    GoodsOrder.STEP_COMPLETED -> {
                        tvDeleteOrder.visibility = View.VISIBLE
                        tvGoEvaluate.visibility = View.VISIBLE
                    }
                    GoodsOrder.STEP_CANCELED -> {
                        tvDeleteOrder.visibility = View.VISIBLE
                    }
                    GoodsOrder.STEP_RETURNING -> {
//                        tvDeleteOrder.visibility = View.VISIBLE
                    }
                    GoodsOrder.STEP_RETURNED -> {
                        tvDeleteOrder.visibility = View.VISIBLE
                    }

                }
            }
        }
        tvDeleteOrder.setOnClickListener { deleteGoodsOrder(mOrderId) }
        tvGoPay.setOnClickListener {
            var dialog = PayDialog(this)
            dialog.setCallback(object : MCallBack<String> {
                override fun call(t: String, position: Int) {
                    Util.goodsPay(
                        this@GoodsOrderDetailActivity,
                        mOrder?.orderno ?: "",
                        t,
                        object : OnPayResultListener {
                            override fun onSuccess() {
                                toast("支付成功")
                                finish()
                                Router.openUI(
                                    this@GoodsOrderDetailActivity,
                                    StoreOrderActivity::class.java,
                                    HashMap<String, Int>().apply {
                                        put(
                                            StoreOrderActivity.ORDERTAG,
                                            2
                                        )
                                    })
                            }

                            override fun onFail(err: String) {
                                toast(err)
                            }

                            override fun onCancel() {
                                toast("支付取消")
                            }

                        })
                }
            })
            dialog.show()
        }
        tvRemindSend.setOnClickListener { mPresenter?.remindSend(mOrder?.id ?: "") }
        tvCancelOrder.setOnClickListener { cancelGoodsOrder(mOrderId) }
        tvConnectService.setOnClickListener {
            Util.callPhone(
                this,
                Constant.CUSTOMERSERVICE_MOBILE
            )
        }
        tvConfirmReceived.setOnClickListener { mPresenter?.confirmReceived(mOrder?.id ?: "") }
        tvGoEvaluate.setOnClickListener {
            var bundle = Bundle().apply {
                putString(EvaluateListActivity.ORDERNO_KEY, mOrder?.orderno ?: "")
                putString(EvaluateListActivity.ORDERID_KEY, mOrder?.id ?: "")
                putSerializable(EvaluateListActivity.GOODSLIST_KEY, mOrder?.ordersGoods)
            }
            Router.openUI(this, EvaluateListActivity::class.java, bundle)
        }


    }

    private fun deleteGoodsOrder(orderId: String) {
        var dialog = TipDialog(this).apply {
            getDescView()?.text = "确定删除该订单吗？"
            getLeftView()?.apply {
                text = "取消"
                setOnClickListener { dismiss() }
            }
            getRightView()?.apply {
                text = "确定"
                setOnClickListener {
                    dismiss()
                    mPresenter?.deleteOrder(orderId)
                }
            }
        }
        dialog.show()
    }

    private fun cancelGoodsOrder(orderId: String) {
        var dialog = TipDialog(this).apply {
            getDescView()?.text = "确定取消该订单吗？"
            getLeftView()?.apply {
                text = "取消"
                setOnClickListener { dismiss() }
            }
            getRightView()?.apply {
                text = "确定"
                setOnClickListener {
                    dismiss()
                    mPresenter?.cancelGoodsOrder(orderId)
                }
            }
        }
        dialog.show()
    }

    override fun onDeletGoodsOrderSuccess() {
        toast("删除订单成功")
        onBackPressed()
    }

    override fun onCancelGoodsOrderSuccess() {
        toast("取消订单成功")
        onBackPressed()
    }

    override fun onConfirmReceivedSuccess() {
        toast("已确认收货")
    }

    override fun onRemindSuccess() {
        toast("已提醒卖家发货")
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

}
